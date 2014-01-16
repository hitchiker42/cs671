functor TestEnv (Env : ENVIRONMENT) (* sample env tests *)
 : sig val tests : (unit -> bool) list end = struct

open Env

val e = addVar(("x", "foo"),
               addOp(("&",String.^),
                     addVar(("x","FOO"),
                            addOp(("&",fn _ => ""),
                                  addVar(("y","bar"),
                                         emptyEnv)))))

fun test01 () = getVar e "x" = "foo"

fun test02 () = (getVar e "X"; false) handle SymNotFound name => name="X"

fun test03 () = (getOp e "&&"; false) handle SymNotFound name => name="&&"

fun test04 () = getOp e "&" ("a", "b") = "ab"

fun test05 () = (getVar (remVar("x", e)) "x"; false)
    handle SymNotFound name => name="x"

val tests = [test01, test02, test03, test04, test05]
end


functor TestSuperEnv (Env : SUPER_ENVIRONMENT) (* sample "super" env tests *)
 : sig val tests : (unit -> bool) list end = struct

open Env

local
val shift = ord #"a"
in
fun f s =
    case explode s of
      (x::_) => SOME (ord x - shift)
    | _ => NONE
end

fun g s =
    case explode s of
      (#"+"::_) => SOME Int.+
    | _ => NONE

val e = addVars(f,addOps(g,emptyEnv))

fun test01 () = getVar e "x" = 23

fun test02 () = (getVar e ""; false) handle SymNotFound name => name=""

fun test03 () = (getOp e "&"; false) handle SymNotFound name => name="&"

fun test04 () = getOp e "+" (2, 3) = 5

fun test05 () = (getVar (remVar("x", e)) "x"; false)
    handle SymNotFound name => name="x"

local
structure S = TestEnv(Env)
in
val tests = S.tests @ [test01, test02, test03, test04, test05]
end

end


(* sample expression tests *)
structure TestExpr : sig val tests : (unit -> bool) list end = struct

structure E = Expression(val specials = "+-*/")
open E

val s1 = "(aa++(b*c))-2"
val e1 = parse Int.fromString s1

fun test01 () = e1=Bex ("-",Bex ("++",Vex "aa",Bex ("*",Vex "b",Vex "c")),Cex 2)
fun test02 () = prettyPrint Int.toString e1 = "aa ++ (b * c) - 2"
fun test03 () = List.all (isIdentifier Int.fromString)
                ["m", "**", "+-+-", "x2", "m1m", "#w", "l&", "."]
fun test04 () = not (List.exists (isIdentifier Int.fromString)
                ["12", "a*", "+m+m", "x2/", "m*m", "w x"])
fun test05 () = (parse Int.fromString "(aa++(b*c))-"; false)
                handle Parse _ => true

val tests = [test01, test02, test03, test04, test05]
end


(* sample interpreter tests *)
structure TestInterpreter : sig val tests : (unit -> bool) list end = struct

structure E = Expression(val specials = "+-*/")
structure I = Interpreter(structure Env = ListEnv and E = E)
open I

local
    open E
    open ListEnv
in
val e1 = Bex ("-",Bex ("++",Vex "aa",Bex ("*",Vex "b",Vex "c")),Cex 2)
val vars = [("aa",3),("b",5),("d",8)]
val ops = [("++",Int.+),("*",Int.* )]
val env = List.foldl addVar (List.foldl addOp emptyEnv ops) vars
val env' = addOp (("-",Int.-),env)
val env'' = addVar (("c",5),env')
end

fun test01 () = (eval env e1; false)
                handle Eval msg => String.isSubstring "-" msg
                            orelse String.isSubstring "c" msg

fun test02 () = (eval env' e1; false)
                handle Eval msg => String.isSubstring "c" msg

fun test03 () = eval env'' e1 = 26

val tests = [test01, test02, test03]
end


(* sample compiler tests *)
structure TestCompiler : sig val tests : (unit -> bool) list end = struct

structure E = Expression(val specials = "+-*/")
structure C = Compiler(structure Env = ListEnv and E = E)
open C

local
    open E
    open ListEnv
in
val e1 = Bex ("-",Bex ("++",Vex "aa",Bex ("*",Vex "b",Vex "c")),Cex 2)
val p1 = compile e1
val vars = [("aa",3),("b",5),("d",8)]
val ops = [("++",Int.+),("*",Int.* )]
val env = List.foldl addVar (List.foldl addOp emptyEnv ops) vars
val env' = addOp (("-",Int.-),env)
val env'' = addVar (("c",5),env')
end

fun test01 () = (run env p1; false)
                handle Runtime msg => String.isSubstring "-" msg
                               orelse String.isSubstring "c" msg

fun test02 () = (run env' p1; false)
                handle Runtime msg => String.isSubstring "c" msg

fun test03 () = run env'' p1 = 26

fun test04 () = run env [Load "aa", Load "d", Const 10, Apply "*", Apply "++"]
                = 83

fun test05 () = (run env [Load "aa", Apply "*"]; false)
                handle Runtime _ => true

val tests = [test01, test02, test03, test04, test05]
end

local
  structure T = TestEnv(ListEnv)
  fun testLoop () = let val f : unit -> unit = run in true end
  val tests = testLoop ::
          T.tests @ TestExpr.tests @ TestInterpreter.tests @ TestCompiler.tests
in
val result = List.all (fn t => t()) tests
end

(* uncomment to test bonus *)
(*
local
  structure T = TestEnv(SuperEnv)
  structure V = TestSuperEnv(SuperEnv)
  val tests = T.tests @ V.tests
in
val resultBonus = List.all (fn t => t()) tests
end
*)
