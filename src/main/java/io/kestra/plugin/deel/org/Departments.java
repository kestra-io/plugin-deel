package io.kestra.plugin.deel.org;

import com.fasterxml.jackson.core.type.TypeReference;
import io.kestra.core.models.annotations.Example;
import io.kestra.core.models.annotations.Plugin;
import io.kestra.core.models.annotations.PluginProperty;
import io.kestra.core.models.property.Property;
import io.kestra.core.models.tasks.RunnableTask;
import io.kestra.core.models.tasks.common.FetchType;
import io.kestra.core.runners.RunContext;
import io.kestra.core.serializers.FileSerde;
import io.kestra.plugin.deel.connection.AbstractDeelConnection;
import io.kestra.plugin.deel.model.DeelDepartment;
import io.kestra.plugin.deel.model.DeelListResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.slf4j.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@SuperBuilder
@Getter
@NoArgsConstructor
@Schema(
    title = "List Departments",
    description = "List departments within the authenticated user's organization. Requires the organizations:read scope."
)
@Plugin(
    examples = {
        @Example(
            title = "List departments",
            full = true,
            code = """
                id: list_departments
                namespace: company.team
                tasks:
                  - id: list_departments
                    type: io.kestra.plugin.deel.org.Departments
                    apiToken: "{{ secret('DEEL_API_TOKEN') }}"
                    fetchType: FETCH
                """
        )
    }
)
public class Departments extends AbstractDeelConnection implements RunnableTask<Departments.Output> {

    @Schema(
        title = "Result handling mode",
        description = "Controls how hits are exposed in outputs. FETCH returns all hits in the response. FETCH_ONE returns only the first hit. STORE writes hits to Kestra storage and returns a URI. NONE leaves outputs empty."
    )
    @PluginProperty(group = "execution")
    @Builder.Default
    private Property<FetchType> fetchType = Property.ofValue(FetchType.FETCH);

    private static final TypeReference<DeelListResponse<DeelDepartment>> DEPARTMENTS_TYPE_REF = new TypeReference<>() {};

    @Override
    public Output run(RunContext runContext) throws Exception {
        Logger logger = runContext.logger();

        DeelListResponse<DeelDepartment> response = request(
            runContext,
            "/departments",
            "GET",
            Map.of(),
            DEPARTMENTS_TYPE_REF
        );

        List<DeelDepartment> departments = response != null && response.getData() != null
            ? response.getData()
            : List.of();

        logger.debug("Retrieved {} departments", departments.size());

        FetchType resolvedFetchType = runContext.render(this.fetchType).as(FetchType.class).orElse(FetchType.FETCH);
        return handleFetch(runContext, departments, resolvedFetchType);
    }

    private Output handleFetch(RunContext runContext, List<DeelDepartment> departments, FetchType fetchType) throws Exception {
        List<Map<String, Object>> mapped = departments.stream()
            .map(department -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", department.getId());
                map.put("name", department.getName());
                map.put("parent", department.getParent());
                return map;
            })
            .toList();

        return switch (fetchType) {
            case FETCH -> Output.builder()
                .rows(mapped)
                .size(mapped.size())
                .total((long) mapped.size())
                .build();
            case FETCH_ONE -> Output.builder()
                .row(mapped.isEmpty() ? null : mapped.getFirst())
                .size(mapped.isEmpty() ? 0 : 1)
                .total((long) mapped.size())
                .build();
            case STORE -> {
                File tempFile = runContext.workingDir().createTempFile(".ion").toFile();
                try (BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(tempFile), FileSerde.BUFFER_SIZE)) {
                    for (Map<String, Object> item : mapped) {
                        FileSerde.write(output, item);
                    }
                }
                URI uri = runContext.storage().putFile(tempFile);
                yield Output.builder()
                    .uri(uri)
                    .size(mapped.size())
                    .total((long) mapped.size())
                    .build();
            }
            case NONE -> Output.builder()
                .size(0)
                .total((long) mapped.size())
                .build();
        };
    }

    @Builder
    @Getter
    public static class Output implements io.kestra.core.models.tasks.Output {

        @Schema(
            title = "Number of departments returned in this response",
            description = "Number of departments included in outputs for the selected fetch type."
        )
        private Integer size;

        @Schema(
            title = "Total number of departments",
            description = "Total departments reported by the API."
        )
        private Long total;

        @Schema(
            title = "Fetched departments",
            description = "Available only when fetchType=FETCH; contains the departments."
        )
        private List<Map<String, Object>> rows;

        @Schema(
            title = "First department",
            description = "Available only when fetchType=FETCH_ONE; contains the first department."
        )
        private Map<String, Object> row;

        @Schema(
            title = "Stored departments URI",
            description = "Available only when fetchType=STORE; Kestra internal storage path to the Ion file."
        )
        private URI uri;
    }
}
