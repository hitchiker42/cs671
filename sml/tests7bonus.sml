use "testenv.sml";

functor TestSuperEnv (Env : SUPER_ENVIRONMENT)
 : sig val tests : (int * string * (unit -> bool)) list end = struct

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

fun test06 () = getVar (remVar("x", e)) "xx" = 23

fun test07 () = let val a = addVar(("+",1),e)
                in getOp a "+" (getVar a "+", getVar a "+") = 2 end

fun test08 () = getVar (remVar("xx",e)) "x" = 23

fun testMany _ _ 0 = true
  | testMany f v n = getOp e f (getVar e v, 10) = 33
                     andalso testMany (f^".") (v^".") (n-1)

fun test09 () = testMany "+" "x" 10000

fun test10 () = let val a = addOp(("+++",Int.* ), addVar(("xxx",2), e))
                in getOp a "+++" (getVar a "x", getVar a "xxx") = 46 end

local
structure S = TestEnv(Env)
in
val tests = S.tests @ [
  (2, "getVar after addVars", test01),
  (1, "getVar of unknown var", test02),
  (1, "getOp of unknown op", test03),
  (2, "getOp after addOps", test04),
  (2, "getVar of removed var", test05),
  (2, "getVar after remVar", test06),
  (2, "getVar after remVar", test08),
  (1, "var and op same name no addOp", test07),
  (1, "var and op same name w/ addOp", test10)
]
end
end


structure T = TestSuperEnv(SuperEnv)
val testsBonus = T.tests

