(*top level definions and load misc structures*)
val load = use
(*useful infix operators*)
infix += fun a += b = a:=(!a)+b
infix -= fun a -= b = a:= (!a)-b
infix != fun a != b = a<>b
infix %  fun a % b = a mod b
infix ::= fun a ::= b = a := (b::(!a))
(*given a function f of a pair
 a <\f\> b is the same as f(a,b) or a </f/>*)
infix  3 <\     fun x <\ f = fn y => f (x, y)     (* Left section      *)
infix  3 \>     fun f \> y = f y                  (* Left application  *)
infixr 3 />     fun f /> y = fn x => f (x, y)     (* Right section     *)
infixr 3 </     fun x </ f = f x                  (* Right application *)
infix  2 o  (* See motivation below *)
infix  0 :=
(* inverse :: *)
infix :-:       fun l :-: e = e :: l
(* c style short circut and/or*)
infix || fun a || b = a orelse b 
infix && fun a && b = a andalso b 
(*prefix operators from builtin infixes*)
fun add x y = op + (x,y)
fun sub x y = op - (x,y)
fun eql x y = op = (x,y)
fun mult x y = op * (x,y)
fun dv x y = op div (x,y)
fun car x = hd x
fun cdr x = tl x
fun cons x y = op :: (x,y)
(*pop head off of a mutable list & return the removed element*)
fun ncar x = let 
  val y = (!x)
in (x:=(cdr y);car y) end
val nhd = ncar
val pop = ncar
fun push x y = (x::=y)
fun snd (a,b) = b
fun fst (a,b) = a
fun id x = x
fun gen_id x = fn () => x
fun to_unit (f,x) = fn ()=> f(x)
val len = List.length
(*fun run_tests (t:(unit->bool) list) = let
    val tests = ListPair.zip (t,(List.map Int.toString (seq 1 (List.length t))))
    fun test (t,i) = if t() then concat ("Test"::i::"Passed"::[]) else
                     concat ("Test"::i::"Failed"::[])
in List.map test tests end
fun print_list (l:string list)=let
    val strings=ref l
in while not ((!strings)=[]) do
         print (concat ((des_car(strings))::"\n"::[])) end
*)
