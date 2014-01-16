val load = use
val _ = load "load.sml"
val _ = load "sigs.sml"
structure ListEnv:ENVIRONMENT = struct
type 'a env = ((string*'a) list)*((string*('a*'a->'a)) list)
val emptyEnv = ([],[])
exception SymNotFound of string
fun addOp (oper,(environ: 'a env)) = (#1(environ),(oper::(#2 environ)))
fun addVar (var,(environ: 'a env)) = ((var::(#1 environ)),#2(environ))
fun getOp (environ:'a env) name = let
    fun find (x:string*('a * 'a -> 'a)) = (#1x = name)
in
    #2(Option.valOf (List.find find (#2 environ)))
    handle Option => raise SymNotFound name end
fun getVar (environ:'a env) name = let
    val find = fn (x:string*'a) => (#1x = name)
in
    #2(Option.valOf (List.find find (#1 environ)))
    handle Option => raise SymNotFound name end
fun remOp (str,(environ:'a env)) = let
    val item = (str,(getOp environ str))
    val rem = fn (x:string*('a*'a->'a)) =>((#1x) != (#1item))
in ((#1 environ),(List.filter rem (# 2environ)))
   handle SymNotFound _ => environ end
fun remVar (str,(environ:'a env)) = let
    val (item:string*'a) = (str,(getVar environ str))
    val rem = fn (x:string*'a) =>not (#1x = #1item)
in (List.filter rem (#1 environ),(#2 environ))
   handle SymNotFound _ => environ end
end
