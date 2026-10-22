# Architecture index

This index is not the full contract. Do not implement from this file alone; follow the artifact paths below.

## Implementation Guide

### Global artifacts

- `unit_graph.yaml`: boundary, trigger, signatures, dependencies and shared references; filter by assigned `name`.
- `migration_boundary.yaml`: future implementation scope; use `must_rewrite`, not `source_anchors`.
- `wire_contracts.yaml`: filter rows by `unit`; preserve transaction and SQL side effects.
- `shared_modules.yaml`: filter `used_by_units`; treat `san-pham-persistence` as a god-class/split candidate.
- `cross_unit_state.yaml`: filter flows where writer or reader equals the assigned unit; preserve matched database state.
- `data-model.md`, `project-structure.md`, `tech-stack.md`: existing schema and runtime context.

### Unit: nhap-hang-page

- external trigger: `NhapHangPanel` create-import tab and its import action.
- must read:
  - `units/nhap-hang-page/behavior.yaml`: validation, writes, rollback and transaction boundary.
  - `units/nhap-hang-page/bindings.yaml`: Swing panel binding.
  - `units/nhap-hang-page/unit_decomposition.yaml`: advisory split candidates only; `commit: false`.
- relevant global rows: `wire_contracts.yaml` unit `nhap-hang-page`; shared modules used by this unit; cross-unit flows written by this unit.
- before DONE report: artifacts read, every `must_preserve` side effect implemented, deferred schema decisions, and build/runtime evidence.

### Unit: ban-hang-page

- external trigger: `BanHangPanel.checkout()` from the payment action.
- must read:
  - `units/ban-hang-page/behavior.yaml`: price/discount branches, invoice writes, stock decrement, points and rollback.
  - `units/ban-hang-page/bindings.yaml`: Swing panel binding.
  - `units/ban-hang-page/unit_decomposition.yaml`: advisory split candidates only; `commit: false`.
- relevant global rows: `wire_contracts.yaml` unit `ban-hang-page`; shared modules used by this unit; cross-unit flows read by this unit.
- before DONE report: artifacts read, every `must_preserve` side effect implemented, FIFO allocation decision, and build/runtime evidence.

