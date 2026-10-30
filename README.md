# Kestra Deel Plugin

Provides Kestra tasks and triggers for Deel HR and payroll management.

## Overview

The Kestra Deel plugin integrates with the [Deel API](https://www.deel.com/) to provide access to HR, payroll, contracts, and organizational data. It includes tasks for listing and retrieving data, and triggers for polling-based and webhook-based event handling.

Plugin classes are under `io.kestra.plugin.deel`.

## Authentication

Configure authentication using a Deel API token:

- **Property**: `apiToken`
- **Type**: `Property<String>`
- **Required**: Yes for all tasks and triggers that call the Deel API
- **Example**: `{{ secret('DEEL_API_TOKEN') }}`

The API token must have the appropriate scopes for the operations being performed (e.g., `timesheets:read`, `contracts:read`, `organizations:read`, `payments:statements:read`, `invoices:read`, `lookups:read`, `worker:read`, `contracts:read`).

## Available Tasks

### Contracts

| Task | Fully Qualified Name | Description |
|------|---------------------|-------------|
| `ContractsList` | `io.kestra.plugin.deel.contracts.ContractsList` | List contracts with filtering and cursor-based pagination |
| `ContractsGet` | `io.kestra.plugin.deel.contracts.ContractsGet` | Retrieve a single contract by ID |

### People

| Task | Fully Qualified Name | Description |
|------|---------------------|-------------|
| `PeopleList` | `io.kestra.plugin.deel.people.PeopleList` | List people with employment details and offset-based pagination |
| `PeopleGet` | `io.kestra.plugin.deel.people.PeopleGet` | Retrieve a single person by ID |

### Invoices

| Task | Fully Qualified Name | Description |
|------|---------------------|-------------|
| `List (Invoices)` | `io.kestra.plugin.deel.invoices.List` | List workforce invoices with status, contract, and date filtering |

### Documents (EOR Contract Documents)

| Task | Fully Qualified Name | Description |
|------|---------------------|-------------|
| `List (Documents)` | `io.kestra.plugin.deel.documents.List` | List HRX documents for an EOR contract with cursor-based pagination |
| `Download` | `io.kestra.plugin.deel.documents.Download` | Download an HRX document as PDF using a pre-signed URL |

### Timesheets

| Task | Fully Qualified Name | Description |
|------|---------------------|-------------|
| `List (Timesheets)` | `io.kestra.plugin.deel.timesheets.List` | List timesheets with status and date filtering |

### Payments

| Task | Fully Qualified Name | Description |
|------|---------------------|-------------|
| `GetStatement` | `io.kestra.plugin.deel.payments.GetStatement` | Retrieve a payment statement by ID |

### Lookups

| Task | Fully Qualified Name | Description |
|------|---------------------|-------------|
| `Get` | `io.kestra.plugin.deel.lookups.Get` | Retrieve platform reference data: countries, currencies, job titles, seniorities |

### Organization

| Task | Fully Qualified Name | Description |
|------|---------------------|-------------|
| `CostCenters` | `io.kestra.plugin.deel.org.CostCenters` | List cost centers for a legal entity |
| `Teams` | `io.kestra.plugin.deel.org.Teams` | List teams in the organization |
| `LegalEntities` | `io.kestra.plugin.deel.org.LegalEntities` | List legal entities with pagination |
| `Managers` | `io.kestra.plugin.deel.org.Managers` | List managers in the organization |
| `Departments` | `io.kestra.plugin.deel.org.Departments` | List departments in the organization |

## Available Triggers

### Polling Triggers

| Trigger | Fully Qualified Name | Description |
|---------|---------------------|-------------|
| `ContractTrigger` | `io.kestra.plugin.deel.contracts.ContractTrigger` | Poll contracts and trigger on created, signed, and terminated events |
| `InvoiceIssuedTrigger` | `io.kestra.plugin.deel.invoices.InvoiceIssuedTrigger` | Poll invoices and trigger on newly issued events |
| `PersonTrigger` | `io.kestra.plugin.deel.people.PersonTrigger` | Poll people and trigger on created and hiring status changes |
| `TimeOffTrigger` | `io.kestra.plugin.deel.timeoff.TimeOffTrigger` | Poll time-off profiles and trigger on requested/approved events |

### Webhook Trigger

| Trigger | Fully Qualified Name | Description |
|---------|---------------------|-------------|
| `WebhookTrigger` | `io.kestra.plugin.deel.webhooks.WebhookTrigger` | Accept incoming Deel webhook requests and verify HMAC-SHA256 signatures |

## YAML Example

List approved timesheets for a contract in a date range:

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

Initialize webhook-based contract event monitoring:

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

## Building and Running Locally

1. **Build the shadow JAR**: `./gradlew shadowJar`
   - Output: `build/libs/`

2. **Run with Docker Compose**:
   - `docker compose up`
   - Builds `kestra/kestra:latest` and mounts `build/libs/` to `/app/plugins/`
   - Kestra UI available at [localhost:8080](http://localhost:8080)

3. **Local development notes**:
   - Mounting a host folder onto `/app/plugins/` replaces the container's plugins directory
   - If a flow depends on another plugin, copy its jar into `build/libs/` too before starting
   - JFR repository error on some hosts: add `/tmp` tmpfs mount or `-v /tmp:/tmp`

## Links

- [Deel API Documentation](https://www.deel.com/api)
- [Kestra Plugin Developer Guide](https://kestra.io/docs/plugin-developer-guide)
- [Kestra Documentation](https://kestra.io/docs)
- [License: Apache 2.0](https://github.com/kestra-io/kestra/blob/develop/LICENSE)

## Stay up to date

New versions released monthly. Star the [main repository](https://github.com/kestra-io/kestra) for notifications.