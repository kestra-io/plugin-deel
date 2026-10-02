package io.kestra.plugin.deel.documents;

import com.fasterxml.jackson.core.type.TypeReference;
import io.kestra.core.models.annotations.Example;
import io.kestra.core.models.annotations.Plugin;
import io.kestra.core.models.annotations.PluginProperty;
import io.kestra.core.models.property.Property;
import io.kestra.core.models.tasks.RunnableTask;
import io.kestra.core.runners.RunContext;
import io.kestra.plugin.deel.connection.AbstractDeelConnection;
import io.kestra.plugin.deel.model.DeelHrxDownloadResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.slf4j.Logger;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.Map;

@SuperBuilder
@Getter
@NoArgsConstructor
@Schema(
    title = "Download EOR Contract Document",
    description = "Download an HRX document for an EOR contract as a PDF. The API returns a pre-signed URL valid for 15 minutes; the file content is stored in Kestra storage. Requires the contracts:read and worker:read scopes."
)
@Plugin(
    examples = {
        @Example(
            title = "Download a contract document",
            full = true,
            code = """
                id: download_document
                namespace: company.team
                tasks:
                  - id: download_document
                    type: io.kestra.plugin.deel.documents.Download
                    apiToken: "{{ secret('DEEL_API_TOKEN') }}"
                    contractId: "123E4567"
                    documentId: "123e4567-e89b-12d3-a456-426614174000"
                """
        )
    }
)
public class Download extends AbstractDeelConnection implements RunnableTask<Download.Output> {

    @Schema(
        title = "Contract ID",
        description = "The unique identifier of the EOR employee contract."
    )
    @PluginProperty(group = "filter")
    private Property<String> contractId;

    @Schema(
        title = "Document ID",
        description = "The unique identifier of the document to download."
    )
    @PluginProperty(group = "filter")
    private Property<String> documentId;

    private static final TypeReference<DeelHrxDownloadResponse> DOWNLOAD_TYPE_REF = new TypeReference<>() {};

    @Override
    public Output run(RunContext runContext) throws Exception {
        Logger logger = runContext.logger();

        String renderedContractId = runContext.render(this.contractId).as(String.class).orElseThrow();
        String renderedDocumentId = runContext.render(this.documentId).as(String.class).orElseThrow();

        DeelHrxDownloadResponse response = request(
            runContext,
            "/rest/eor/contracts/" + renderedContractId + "/hrx-documents/" + renderedDocumentId,
            "GET",
            Map.of(),
            DOWNLOAD_TYPE_REF
        );

        if (response == null || response.getData() == null || response.getData().getUrl() == null) {
            throw new IllegalStateException("Download URL not found for document: " + renderedDocumentId);
        }

        String downloadUrl = response.getData().getUrl();
        logger.debug("Downloading document {} for contract {}", renderedDocumentId, renderedContractId);

        byte[] content = downloadBytes(downloadUrl);

        File tempFile = runContext.workingDir().createTempFile(".pdf").toFile();
        Files.write(tempFile.toPath(), content);
        URI uri = runContext.storage().putFile(tempFile);

        return Output.builder()
            .uri(uri)
            .url(downloadUrl)
            .contractId(renderedContractId)
            .documentId(renderedDocumentId)
            .size((long) content.length)
            .build();
    }

    private byte[] downloadBytes(String downloadUrl) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(URI.create(downloadUrl))
            .GET()
            .build();

        HttpResponse<byte[]> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());

        if (httpResponse.statusCode() < 200 || httpResponse.statusCode() >= 300) {
            throw new IllegalStateException("Document download failed with HTTP " + httpResponse.statusCode());
        }

        return httpResponse.body();
    }

    @Builder
    @Getter
    public static class Output implements io.kestra.core.models.tasks.Output {

        @Schema(
            title = "Stored document URI",
            description = "Kestra internal storage path to the downloaded PDF file."
        )
        private URI uri;

        @Schema(
            title = "Pre-signed download URL",
            description = "Pre-signed URL returned by the API. Valid for 15 minutes."
        )
        private String url;

        @Schema(
            title = "Contract ID",
            description = "The EOR contract the document belongs to."
        )
        private String contractId;

        @Schema(
            title = "Document ID",
            description = "The downloaded document."
        )
        private String documentId;

        @Schema(
            title = "File size in bytes",
            description = "Size of the downloaded file."
        )
        private Long size;
    }
}
