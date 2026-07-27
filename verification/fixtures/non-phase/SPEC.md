# Widget compatibility contract

## Required behavior

The widget accepts the names `alpha` and `beta`. An unknown name produces `unsupported` and never
changes the stored value.

## Completion gate

The target must name both accepted values, the unknown-name result, and the no-mutation guarantee.
