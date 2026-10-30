# Kestra Deel Plugin Documentation

## Overview

The Kestra Deel plugin provides integration with the [Deel](https://www.deel.com/) HR and payroll platform. It enables Kestra flows to interact with Deel's API for managing contracts, people, invoices, payments, time off, and organizational data.

Plugin classes are under the `io.kestra.plugin.deel` package.

## Authentication

All tasks and triggers that call the Deel API require an API token.

- **Property**: `apiToken`
- **Type**: `Property<String>`
- **Required**: Yes for API-enabled tasks and triggers
- **Common pattern**: `{{ secret('DEEL_API_TOKEN') }}`

The API token must possess the appropriate Deel scopes for the intended operations (e.g., `timesheets:read`, `contracts:read`, `organizations:read`, `payments:statements:read`, `invoices:read`, `lookups:read`, `worker:read`).

## Tasks

### Contracts

| Task | Fully Qualified Name | Description |
|------|---------------------|-------------|
| **ContractsList** | `io.kestra.plugin.deel.contracts.ContractsList` | List contracts with filtering by type, status, legal entity, or team. Supports cursor-based pagination via `afterCursor`. |
| **ContractsGet** | `io.kestra.plugin.deel.contracts.ContractsGet` | Retrieve a single contract by its Deel-assigned UUID ID. |

### People

| Task | Fully Qualified Name | Description |
|------|---------------------|-------------|
| **PeopleList** | `io.kestra.plugin.deel.people.PeopleList` | List people in the organization with employment details. Supports offset-based pagination using `limit` and `offset`. Filter by hiring status, hiring type, legal entity, or search term. |
| **PeopleGet** | `io.kestra.plugin.deel.people.PeopleGet` | Retrieve a single person (worker) by their Deel person ID UUID. |

### Invoices

| Task | Fully Qualified Name | Description |
|------|---------------------|-------------|
| **List (Invoices)** | `io.kestra.plugin.deel.invoices.List` | List workforce invoices. Supports filtering by status (`paid` by default, or `all`), contract ID, and date ranges (issued from/to). Supports offset- and cursor-based pagination. |

### Documents (EOR Contract Documents)

| Task | Fully Qualified Name | Description |
|------|---------------------|-------------|
| **List (Documents)** | `io.kestra.plugin.deel.documents.List` | List HRX documents shared under an EOR contract. Requires `contractId`. Supports cursor-based pagination via `cursor`. |
| **Download** | `io.kestra.plugin.deel.documents.Download` | Download an HRX document as a PDF. Requires `contractId` and `documentId`. The API returns a pre-signed URL valid for 15 minutes; the file content is stored in Kestra storage. Requires `contracts:read` and `worker:read` scopes. |

### Timesheets

| Task | Fully Qualified Name | Description |
|------|---------------------|-------------|
| **List (Timesheets)** | `io.kestra.plugin.deel.timesheets.List` | List timesheets in the account. Supports filtering by `contractId`, `status` (approved, declined, not_payable, paid, pending, processing), and date ranges (`dateFrom`, `dateTo`). Supports offset-based pagination via `limit` and `offset`. |

### Payments

| Task | Fully Qualified Name | Description |
|------|---------------------|-------------|
| **GetStatement** | `io.kestra.plugin.deel.payments.GetStatement` | Retrieve a payment statement by its unique ID. Includes status, amounts, beneficiary details, and associated settled invoices. Requires `payment-statements:read` scope. |

### Lookups

| Task | Fully Qualified Name | Description |
|------|---------------------|-------------|
| **Get** | `io.kestra.plugin.deel.lookups.Get` | Retrieve platform reference data. Supported `lookupType` values: `COUNTRIES`, `CURRENCIES`, `JOB_TITLES`, `SENIORITIES`. The `JOB_TITLES` endpoint paginates via `afterCursor`. The `SENIORITIES` endpoint supports `isEorContract` filter (excludes C-level when true). |

### Organization

| Task | Fully Qualified Name | Description |
|------|---------------------|-------------|
| **CostCenters** | `io.kestra.plugin.deel.org.CostCenters` | List cost centers for a specific legal entity. Requires `legalEntityId`. |
| **Teams** | `io.kestra.plugin.deel.org.Teams` | List teams within the authenticated user's organization. |
| **LegalEntities** | `io.kestra.plugin.deel.org.LegalEntities` | List legal entities in the account. Supports filtering by `country`, `entityType`, `legalEntityId`, `globalPayroll`, and `includeArchived`. Supports cursor-based pagination via `cursor`. |
| **Managers** | `io.kestra.plugin.deel.org.Managers` | List managers in the organization. Supports offset-based pagination via `limit` and `offset`. |
| **Departments** | `io.kestra.plugin.deel.org.Departments` | List departments within the authenticated user's organization. |

## Triggers

### Polling Triggers

| Trigger | Fully Qualified Name | Description |
|---------|---------------------|-------------|
| **ContractTrigger** | `io.kestra.plugin.deel.contracts.ContractTrigger` | Poll the Contracts endpoint and trigger on contract lifecycle events. Supports configurable `events` (created, signed, terminated). State (watermark + last-seen statuses) is persisted in the KV store to prevent duplicate emissions. |
| **InvoiceIssuedTrigger** | `io.kestra.plugin.deel.invoices.InvoiceIssuedTrigger` | Poll invoices and trigger on newly issued events. State is persisted in the KV store so that already-seen invoice IDs are tracked and not re-emitted. |
| **PersonTrigger** | `io.kestra.plugin.deel.people.PersonTrigger` | Poll the People endpoint and trigger on created people and hiring status changes. State is persisted in the KV store. Note: the Deel API does not return previous hiring status; it is tracked client-side from earlier polls. |
| **TimeOffTrigger** | `io.kestra.plugin.deel.timeoff.TimeOffTrigger` | Poll the profile time-off endpoint and trigger on requested and approved time-off records. Uses cursor pagination (`page_size`/`next`). State is persisted in the KV store. |

### Webhook Trigger

| Trigger | Fully Qualified Name | Description |
|---------|---------------------|-------------|
| **WebhookTrigger** | `io.kestra.plugin.deel.webhooks.WebhookTrigger` | Accept incoming Deel webhook requests and verify HMAC-SHA256 signatures. Each request is verified using HMAC-SHA256 over the concatenation of the POST method and raw request body, using the webhook signing key. Requests with missing or invalid signatures are rejected without triggering a flow. Configure using `secretSigningKey` and optional `events` filter. |

## Configuration Properties Reference

### Common across tasks and triggers

- **`apiToken`** (`Property<String>`, required): Deel API authentication token.

### Task-specific properties

| Property | Task | Type | Group | Description |
|----------|------|------|-------|-------------|
| `limit` | Various tasks | `Property<Integer>` | `filter` | Maximum records per page (default: 25, max: 100) |
| `offset` | Various tasks | `Property<Integer>` | `filter` | Pagination offset (default: 0) |
| `cursor` | Various tasks | `Property<String>` | `filter` | Cursor for cursor-based pagination |
| `contractId` | Documents.List, Invoices, Timesheets | `Property<String>` | `filter` | Filter by contract ID |
| `status` | Invoices, Timesheets | `Property<String>` | `filter` | Filter by status (e.g., `paid`, `all`, or specific statuses) |
| `dateFrom` | Invoices, Timesheets | `Property<String>` | `filter` | Filter records on or after date (YYYY-MM-DD) |
| `dateTo` | Invoices, Timesheets | `Property<String>` | `filter` | Filter records before date (YYYY-MM-DD) |
| `contractType` | ContractsList | `Property<String>` | `filter` | Filter contracts by type (e.g., `open`, `terminated`, `expired`) |
| `legalEntityId` | Various tasks | `Property<String>` | `filter` | Filter by legal entity ID |
| `teamId` | ContractsList | `Property<String>` | `filter` | Filter contracts by team ID |
| `updatedSince` | ContractsList, PeopleList | `Property<String>` | `filter` | Filter by last updated timestamp |
| `hiringStatus` | PeopleList | `Property<String>` | `filter` | Filter people by hiring status |
| `hiringType` | PeopleList | `Property<String>` | `filter` | Filter by hiring type (employee, contractor, EOR) |
| `search` | PeopleList | `Property<String>` | `filter` | Search people by name, email, or other fields |
| `contractId` | CostCenters (required) | `Property<String>` | `filter` | Legal entity ID whose cost centers to return |
| `country` | LegalEntitiesList | `Property<String>` | `filter` | Filter legal entities by country |
| `entityType` | LegalEntitiesList | `Property<String>` | `filter` | Filter by entity type |
| `globalPayroll` | LegalEntitiesList | `Property<Boolean>` | `filter` | Filter by global payroll flag |
| `includeArchived` | LegalEntitiesList | `Property<Boolean>` | `filter` | Include archived legal entities |
| `events` | ContractTrigger, InvoiceIssuedTrigger, PersonTrigger, TimeOffTrigger | `Property<List<?>>` | `main` | Which events trigger executions |
| `signedStatuses` | ContractTrigger | `Property<List<String>>` | `main` | Contract statuses treated as signed (default: `["in_progress"]`) |
| `terminatedStatuses` | ContractTrigger | `Property<List<String>>` | `main` | Contract statuses treated as terminated (default: `["terminated", "cancelled", "user_cancelled"]`) |
| `interval` | Polling triggers | `Duration` | `main` | Polling interval (default: `PT5M`) |
| `hrisProfileId` | TimeOffTrigger (required) | `Property<String>` | `main` | Worker HRIS profile ID |
| `secretSigningKey` | WebhookTrigger (required) | `Property<String>`, secret | `connection` | Webhook signing key for HMAC-SHA256 verification |
| `lookupType` | Lookups.Get (required) | `Property<LookupType>` | `main` | Enum: `COUNTRIES`, `CURRENCIES`, `JOB_TITLES`, `SENIORITIES` |
| `isEorContract` | Lookups.Get (SENIORITIES) | `Property<Boolean>` | `filter` | When true, excludes C-level seniorities |
| `afterCursor` | Lookups.Get (JOB_TITLES) | `Property<String>` | `filter` | Pagination cursor for job titles |
| `fetchType` | Various tasks | `Property<FetchType>` | `execution` | Result handling: `FETCH`, `FETCH_ONE`, `STORE`, `NONE` (default: `FETCH`) |

## YAML Examples

### List approved timesheets for a contract

```yaml
id: list_contract_timesheets
namespace: company.team
tasks:
  - id: list_timesheets
    type: io.kestra.plugin.deel.timesheets.List
    apiToken: "{{ secret('DEEL_API_TOKEN') }}"
    contractId: "contract_abc123"
    status: "approved"
    dateFrom: "2024-01-01"
    dateTo: "2024-12-31"
    fetchType: STORE
```

### List invoices with status all and date range

```yaml
id: list_invoices
namespace: company.team
tasks:
  - id: list_invoices
    type: io.kestra.plugin.deel.invoices.List
    apiToken: "{{ secret('DEEL_API_TOKEN') }}"
    status: "all"
    issuedFrom: "2024-01-01"
    issuedTo: "2024-12-31"
    fetchType: FETCH
```

### Get a single contract

```yaml
id: get_contract
namespace: company.team
tasks:
  - id: get_contract
    type: io.kestra.plugin.deel.contracts.ContractsGet
    apiToken: "{{ secret('DEEL_API_TOKEN') }}"
    contractId: "550e8400-e29b-41d4-a716-446655440000"
```

### List people with hiring status filter

```yaml
id: list_people
namespace: company.team
tasks:
  - id: list_people
    type: io.kestra.plugin.deel.people.PeopleList
    apiToken: "{{ secret('DEEL_API_TOKEN') }}"
    hiringStatus: "active"
    limit: 50
    fetchType: STORE
```

### Look up currencies

```yaml
id: lookup_currencies
namespace: company.team
tasks:
  - id: lookup_currencies
    type: io.kestra.plugin.deel.lookups.Get
    apiToken: "{{ secret('DEEL_API_TOKEN') }}"
    lookupType: CURRENCIES
```

### Poll contracts for events

```yaml
id: watch_contracts
namespace: company.team
triggers:
  - id: watch_contracts
    type: io.kestra.plugin.deel.contracts.ContractTrigger
    apiToken: "{{ secret('DEEL_API_TOKEN') }}"
    interval: PT5M
    events:
      - created
      - signed
      - terminated
```

### Webhook trigger for Deel events

```yaml
id:deel_webhook_events
namespace: company.team
triggers:
  - id: deel_webhook
    type: io.kestra.plugin.deel.webhooks.WebhookTrigger
    apiToken: "{{ secret('DEEL_API_TOKEN') }}"
    secretSigningKey: "{{ secret('DEEL_WEBHOOK_SIGNING_KEY') }}"
    events:
      - contract.created
      - contract.signed
```

## Building and Running Locally

1. **Build the shadow JAR**: `./gradlew shadowJar`
   - Output: `build/libs/`

2. **Run with Docker Compose**:
   ```bash
   docker compose up
   ```
   - Builds `kestra/kestra:latest` and mounts `build/libs/` to `/app/plugins/`
   - Kestra UI available at [localhost:8080](http://localhost:8080)

3. **Local development notes**:
   - Mounting a host folder onto `/app/plugins/` replaces the container's plugins directory rather than adding to it. Core plugins compiled into Kestra itself are unaffected, but any additional plugin normally bundled in the base image under `/app/plugins/` gets hidden once the mount is in place.
   - If a flow depends on another plugin, copy its jar into `build/libs/` too before starting the container.
   - JFR repository error on some hosts: `Unable to create JFR repository directory using base location (/tmp)`. Work around by mounting `/tmp` as `tmpfs` or adding `-v /tmp:/tmp` or `--tmpfs /tmp` (tracked upstream in [kestra-io/kestra#17405](https://github.com/kestra-io/kestra/issues/17405)).

## Links

- [Deel API Documentation](https://www.deel.com/api)
- [Kestra Plugin Developer Guide](https://kestra.io/docs/plugin-developer-guide)
- [Kestra Documentation](https://kestra.io/docs)
- [License: Apache 2.0](https://github.com/kestra-io/kestra/blob/develop/LICENSE)

## Stay up to date

New versions released monthly. Star the [main repository](https://github.com/kestra-io/kestra) for notifications and future updates.