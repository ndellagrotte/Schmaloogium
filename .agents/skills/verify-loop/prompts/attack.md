{{COMMON}}

Your attack lens is **{{LENS_TITLE}}** (`{{LENS_ID}}`).

{{LENS_INSTRUCTIONS}}

{{SURFACE_CONTEXT}}

{{REVIEW_EXCEPTION}}

Return candidate defects under only this lens. Before returning one, search the entire target for
equivalent coverage and distinguish a real defect from a house-style preference. Give every
candidate a stable `candidate_id` beginning with `{{LENS_ID}}-`. Severity means:

- `blocking`: structural miss requiring a rebuild or making the artifact unusable;
- `correction`: fixable defect that prevents literal PASS;
- `note`: useful observation that does not block PASS and will not be applied by fix-up.

`touches_interface` is your own judgment about whether the proposed correction changes any
manifest-declared interface/change-trigger region.
