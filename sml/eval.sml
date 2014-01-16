val load = use;
val _ = load "load.sml"
val _ = load "sigs.sml"
functor Interpreter (structure Env : ENVIRONMENT
                     structure E : EXPRESSION) : INTERPRETER where type 'a env = 'a Env.env and type 'a expr = 'a E.expr  =
  struct
  (*takes an env & an expr and returns an Interpreter structure*)
  open Env
  open E
  exception Eval of string
  (*use a let block to use env*)
  fun eval env (Cex c) = c
    | eval env (Vex s) = (getVar env s
      handle (SymNotFound name) =>
             raise (Eval (name ^ " is not defined")))
    | eval env (Bex (s,l,r)) =
             getOp env s (eval env l,eval env r)
             handle (SymNotFound name) =>
                    raise (Eval (name ^ " is not defined"))
        end

functor Compiler (structure Env:ENVIRONMENT and E:EXPRESSION) : COMPILER (*where type 'a env = Env.env and type 'a expr = E.expr*) = struct
      (*takes an env & an expr and returns a Compilier structure*)
type 'a env = 'a Env.env
type 'a expr = 'a E.expr
datatype 'a instr = Load of string
                  | Apply of string
                  | Const of 'a
open Env
open E
fun check_empty x = ((car x);true)
                    handle Empty => false
exception Runtime of string
fun compile exp =
    let
      fun comp (Cex c) = ((Const c)::[])
      | comp (Vex v) = ((Load v)::[])
      | comp (Bex (s,l,r)) = ((Apply s)::(comp r)@(comp l)@[])
in rev (comp exp) end
fun run (environ:'a env) (instrs:'a instr list) = let
  val stack = ref []
  val instructs = ref instrs
  fun flip (x,y) = (y,x)
  fun exec (Const c) = (push stack c)
    | exec (Load v) = (push stack (getVar environ v)
                       handle (SymNotFound name) =>
                              raise (Runtime (v ^ " not found")))
    | exec (Apply s) = (push stack ((getOp environ s) (flip((pop stack),(pop stack))))
                        handle (SymNotFound name) =>
                               raise (Runtime (s ^ " not found"))
                             | (Empty) =>
                               raise (Runtime (" Stack underflow")))
  fun start () =
      ((while (check_empty (!instructs)) do
            (exec (pop instructs)));
            if (not(check_empty (!stack)))
                 then raise (Runtime (" Stack overflow"))
            else pop stack )
in start () end
(*    fun run [] a = a
      | run (x::xs) a = a
in  a end*)
        (*compile: 'a expr -> 'a instr list:take an expression and return a list
         *of instructions (instructions on a stack machine)
         *run : 'a env -> 'a instr list -> 'a:runs a program is the given enviroment
         *and returns a value as a result, raises a Runtime excepiton on an error*)
end
