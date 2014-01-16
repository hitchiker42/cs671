(* $Id: tests7.sml 197 2013-04-24 16:09:14Z cs671a $ *)
(* Assignment #7: SML 3 *)


use "testenv.sml";

structure TestExpr
 : sig val tests : (int * string * (unit -> bool)) list end = struct

structure E = Expression(val specials = "+-*/")
open E

val s1 = "(aa++(b*c))-2"
val e1 = parse Int.fromString s1
val ids = ["m", "+++", "*+-+-/", "x2y", "m10", "^w", "l$", ".", "-"]
val s2 = List.foldl (fn (x,a) => a^" "^x) "" ids

fun test01 () = e1=Bex ("-",Bex ("++",Vex "aa",Bex ("*",Vex "b",Vex "c")),Cex 2)
fun test02 () = prettyPrint Int.toString e1 = "aa ++ (b * c) - 2"
fun test03 () = List.all (isIdentifier Int.fromString) ids
fun test04 () = not (List.exists (isIdentifier Int.fromString)
                ["12", "a+", "-m+m", "x2*", "m-m", "w x"])
fun test05 () = (parse Int.fromString "(aa++(b*c))-"; false)
                handle Parse _ => true
fun test06 () = " " ^ prettyPrint Int.toString (parse Int.fromString s2) = s2
fun test07 () = prettyPrint (fn x => x)
                (parse (fn _ => NONE)  "(((a + y -(x+2+3+4)   *(((x))) _ f))+1)")
                = "a + y - (x + 2 + 3 + 4) * x _ f + 1"

val tests = [
    (2, "parse", test01),
    (4, "parse then prettyPrint", test02),
    (2, "isIdentifier with identifiers", test03),
    (2, "isIdentifier with non identifiers", test04),
    (2, "parse w/ exception", test05),
    (4, "tokenize and prettyPrint", test06),
    (4, "prettyPrint", test07)
]
end





structure TestInterpreter
 : sig val tests : (int * string * (unit -> bool)) list end = struct

structure Env = MyListEnv
structure E = MyExpression(val specials = "+-*/")
structure I = Interpreter(structure Env = Env and E = E)
open I

local
    open E
    open Env
in
val e1 = Bex ("-",Bex ("++",Vex "aa",Bex ("*",Vex "b",Vex "c")),Cex 2)
val vars = [("aa",2),("b",7),("d",1)]
val ops = [("++",Int.+),("*",Int.* )]
val env = List.foldl addVar (List.foldl addOp emptyEnv ops) vars
val env' = addOp (("-",Int.-),env)
val env'' = addVar (("c",3),env')

fun build 0 = Cex 0
  | build n = Bex ("++", Vex "d", build (n-1))

fun build2 0 = Cex ""
  | build2 n = Bex (".", build2 (n-1), Cex ".")

val e = addOp ((".", String.^), emptyEnv)
end

fun test01 () = (eval env e1; false)
                handle Eval msg => String.isSubstring "-" msg
                            orelse String.isSubstring "c" msg

fun test02 () = (eval env' e1; false)
                handle Eval msg => String.isSubstring "c" msg

fun test03 () = eval env'' e1 = 21

fun test04 () = eval env (build 1000) = 1000

fun test05 () = let val s = explode (eval e (build2 1000))
                in length s = 1000 andalso List.all (fn x => x = #".") s end

fun test06 () = valOf (eval Env.emptyEnv (E.Cex (SOME 42))) = 42

val tests = [
    (1, "Eval exception", test01),
    (1, "Eval exception", test02),
    (2, "simple eval", test03),
    (2, "eval large int expr", test04),
    (2, "eval large string expr", test05),
    (2, "eval of constant in empty env", test06)
]
end


structure TestCompiler
 : sig val tests : (int * string * (unit -> bool)) list end = struct

structure Env = MyListEnv
structure E = MyExpression(val specials = "+-*/")
structure C = Compiler(structure Env = Env and E = E)
open C

local
    open E
    open Env
in
val e1 = Bex ("-",Bex ("++",Vex "aa",Bex ("*",Vex "b",Vex "c")),Cex 2)
val p1 = compile e1
val vars = [("aa",2),("b",7),("d",1)]
val ops = [("++",Int.+),("*",Int.* )]
val env = List.foldl addVar (List.foldl addOp emptyEnv ops) vars
val env' = addOp (("-",Int.-),env)
val env'' = addVar (("c",3),env')

fun build1 0 = []
  | build1 n = Const n :: build1 (n-1)

fun build2 1 = []
  | build2 n = Apply "++" :: build2 (n-1)
end

fun test01 () = (run env p1; false)
                handle Runtime msg => String.isSubstring "-" msg
                               orelse String.isSubstring "c" msg

fun test02 () = (run env' p1; false)
                handle Runtime msg => String.isSubstring "c" msg

fun test03 () = run env'' p1 = 21

fun test04 () = run env [Load "aa", Load "d", Const 10, Apply "*", Apply "++"]
                = 12

fun test05 () = (run env [Load "aa", Apply "*"]; false)
                handle Runtime _ => true

local
  val prog = [Const 1, Const 2, Load "b", Apply "++"]
in
fun test06 () = run env (tl prog) = 9
fun test07 () = run env prog = 9
fun test08 () = run env (List.take(prog, 3)) = 7
end

fun test09 () = run Env.emptyEnv (compile (E.Cex "x")) = "x"

fun test10 () = run env (build1 1000 @ build2 1000) = 500500

fun test11 () = (run Env.emptyEnv []; false) handle Runtime _ => true

val tests = [
    (1, "Runtime exception", test01),
    (1, "Runtime exception", test02),
    (1, "Runtime exception (short stack)", test05),
    (3, "simple compile/run", test03),
    (3, "simple run", test04),
    (1, "simple run", test06),
    (2, "simple run (long stack)", test07),
    (2, "simple run (long stack)", test08),
    (2, "run of constant in empty env", test09),
    (3, "run of long int prog", test10),
    (1, "run of an empty program", test11)
]

end






structure T = TestEnv(ListEnv)
val allTests =
    T.tests @ TestExpr.tests @ TestInterpreter.tests @ TestCompiler.tests

