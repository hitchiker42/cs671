functor TestEnv (Env : ENVIRONMENT)
 : sig val tests : (int * string * (unit -> bool)) list end = struct

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

fun test06 () = getVar (remVar("y", e)) "x" = "foo"

fun test07 () = let val a = addVar(("&","&"),e)
                in getOp a "&" (getVar a "&", getVar a "&") = "&&" end

fun test08 () = getVar (remVar("X",e)) "x" = "foo"

fun addMany 0 = emptyEnv
  | addMany n = addVar(("x",n), addMany (n-1))

fun test09 () = getVar (addMany 1000000) "x" = 1000000

fun test10 () = (getVar (remVar("x",addMany 1000000)) "x"; false)
    handle SymNotFound name => name = "x"

val tests = [
  (2, "simple getVar", test01),
  (2, "getVar of unknown var", test02),
  (2, "getOp of unknown op", test03),
  (2, "simple getOp", test04),
  (2, "getVar of a removed var", test05),
  (2, "getVar after remove", test06),
  (2, "var and op same names", test07),
  (2, "getVar after remove of nonexistent var", test08),
  (2, "same var added many times", test09),
  (2, "same var added many times then removed", test10)
]

end
