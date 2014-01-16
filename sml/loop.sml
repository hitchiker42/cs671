val _ = use "sigs.sml"
val _ = use "load.sml"
functor Loop (structure Env : ENVIRONMENT
                        and E : EXPRESSION
                        and I : INTERPRETER
                        and C : COMPILER
                          sharing type Env.env = I.env = C.env
                          sharing type E.expr = I.expr = C.expr)
        : sig val run : unit -> unit end = struct
fun run () = let
  exception Parse = E.Parse
  exception Runtime = C.Runtime
  exception Eval = I.Eval
  local
    datatype instr = datatype C.instr in
  fun compToStr (instrs:real instr list) = let
    fun acc ((Const c)::xs) ls = acc xs (("Const"^" "^(Real.toString c))::ls)
      | acc ((Load v)::xs) ls = acc xs (("Load"^" "^v)::ls)
      | acc ((Apply s)::xs) ls = acc xs (("Apply"^" "^s)::ls)
      | acc [] ls = String.concatWith ", " ls
  in acc instrs [] end end
  val EnvLoop =ref (Env.addOp(("+",Real.+),(Env.addOp(("-",Real.-),
               (Env.addOp(("*",Real.* ),(Env.addOp(("/",Real./),
               (Env.addOp(("^",Math.pow),Env.emptyEnv))))))))))
  fun test str = if not(Option.isSome (Real.fromString str)) then if
                     not(Option.isSome(Int.fromString str)) then NONE
                   else SOME(Real.fromInt(Option.valOf(Int.fromString str)))
                 else Real.fromString str
  fun parseReal s =
      let val l = explode s
          fun get [] = NONE
            | get (x::l) = SOME (x,l)
      in
        case Real.scan get l of
            SOME (r, []) => SOME r
          | _ => NONE
      end
(*  fun eql (str:string) (vars:string list) = let
    val str = implode (MiscString.strip str)
    val result = ref vars
    val chars = explode str
    val index = MiscString.find #"=" str
    val var = implode (List.take (chars,index))
    val value = implode (List.drop (chars,(index + 1)))
  in if (MiscString.search #"=" value) then eql value (var::vars)
     else let
       val number = I.eval (!EnvLoop)  (E.parse parseReal value)
       fun env_add ([]:string list) ls = String.concat ls
         | env_add (x::xs) ls = if (E.isIdentifier parseReal x) then
                               (EnvLoop:=(Env.addVar((x,number),(!EnvLoop)));
                                env_add xs (("setting "^x^" to "^(Real.toString number))::ls))
                             else env_add xs (("'"^x^"'"^"is not a valid identifier; ignored")::ls)
     in env_add (!result) ["Something"] end end*)
  fun eql str = let
    val str = implode (MiscString.strip str)
    val chars = ref (explode str)
    val itemp = ref []
    val _ = (while (car (!chars) != #"=") do itemp::=(ncar chars))
    val _ = ncar chars
    val var = implode (!itemp)
    val value = implode (!chars)
    val num = Option.valOf(Real.fromString value)
in (EnvLoop:=(Env.addVar((var,num),(!EnvLoop)));
    ("setting "^var^" to "^(Real.toString num)^"\n")) end
  fun read str = if MiscString.search #"=" str then eql str
                 else let
    val exp = E.parse parseReal str
               handle Parse str =>
                      raise Fail ("cannot parse expression: "^str^"\n")
    val str_exp = ("Expression: "^(E.prettyPrint Real.toString exp))
    val prog = (C.compile exp)
    val comp_str = "Program: "^(compToStr prog)
    val eval = ("Value (eval): "^(Real.toString (I.eval (!EnvLoop) exp)
                                                 handle Eval str =>
                                                        ("cannot evaluate expression: "^str)))
    val comp = ("Value (program): "^(Real.toString (C.run (!EnvLoop) prog)
                                                    handle Runtime str => ("cannot run program: "^str)))
  in (str_exp^"\n"^comp_str^"\n"^eval^"\n"^comp^"\n") end
in case TextIO.inputLine(TextIO.stdIn) of
       (SOME line) => (TextIO.print(read line
handle Fail str => str
| Empty => "Empty\n"
| Eval str =>"Fail: "^str^"\n"); run())
     | NONE => TextIO.print "EOF\n"
end
end
structure loopExpr=Expression (val specials = "+-/*^")
structure myLoop = Loop (structure Env = ListEnv
                           and E = loopExpr and
                           I = Interpreter (structure Env = ListEnv and E = loopExpr)
                           and C = Compiler (structure Env =  ListEnv and E = loopExpr))
val run = myLoop.run
