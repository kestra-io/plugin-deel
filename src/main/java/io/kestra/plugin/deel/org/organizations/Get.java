package io.kestra.plugin.deel.org.organizations;

import com.fasterxml.jackson.core.type.TypeReference;
import io.kestra.core.models.annotations.Example;
import io.kestra.core.models.annotations.Plugin;
import io.kestra.core.runners.RunContext;
import io.kestra.core.models.tasks.RunnableTask;
import io.kestra.plugin.deel.connection.AbstractDeelConnection;
import io.kestra.plugin.deel.model.DeelListResponse;
import io.kestra.plugin.deel.model.DeelOrganization;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuperBuilder
@Getter
@NoArgsConstructor
@Schema(
    title = "Get Current Organization",
    description = "Get details of the organization associated with the authentication token. Requires the organizations:read scope."
)
@Plugin(
    examples = {
        @Example(
            title = "Get the current organization",
            full = true,
            code = """
                id: get_organization
                namespace: company.team
                tasks:
                  - id: get_organization
                    type: io.kestra.plugin.deel.org.organizations.Get
                    apiToken: "{{ secret('DEEL_API_TOKEN') }}"
                """
        )
    }
)
public class Get extends AbstractDeelConnection implements RunnableTask<Get.Output> {

    private static final TypeReference<DeelListResponse<DeelOrganization>> ORGANIZATIONS_TYPE_REF = new TypeReference<>() {};

    @Override
    public Output run(RunContext runContext) throws Exception {
        Logger logger = runContext.logger();

        DeelListResponse<DeelOrganization> response = request(
            runContext,
            "/organizations",
            "GET",
            Map.of(),
            ORGANIZATIONS_TYPE_REF
        );

        List<DeelOrganization> organizations = response != null && response.getData() != null
            ? response.getData()
            : List.of();

        if (organizations.isEmpty()) {
            throw new IllegalStateException("Organization not found for the current token");
        }

        DeelOrganization organization = organizations.getFirst();
        logger.debug("Retrieved organization: {}", organization.getName());

        Map<String, Object> map = new HashMap<>();
        map.put("id", organization.getId());
        map.put("name", organization.getName());

        return Output.builder()
            .organization(map)
            .build();
    }

    @Builder
    @Getter
    public static class Output implements io.kestra.core.models.tasks.Output {

        @Schema(
            title = "Organization details",
            description = "The organization associated with the authentication token."
        )
        private Map<String, Object> organization;
    }
}
