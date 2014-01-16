val load = use
val _ = load "sigs.sml"
val _ = load "load.sml"
structure SuperEnv:SUPER_ENVIRONMENT = struct
include ENVIRONMENT
