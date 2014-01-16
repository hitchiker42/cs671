val load = use
(*useful infix operators*)
infix += fun a += b = a:=(!a)+b
infix -= fun a -= b = a:= (!a)-b
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
structure Misc = struct
val min = fn (i,j)=>if (i<j) then i else j
val max = fn (i,j)=>if (i>j) then i else j
val rand=Random.rand(0,(Int.fromLarge(Time.toSeconds (Time.now ()) mod (Int.toLarge (Option.valOf Int.maxInt)))));
val randInt = fn () => Random.randInt rand
fun loop n x f g h = if f n then h x else loop (g n) (h x) f g h
       (*int n, list x, test fxn y, increment fxn g, and do fxn h*)
fun for n f x = (*do f x n times*) if eql 0 n then x::[] else for (n-1) f (f(x))
fun ifSome [] f = NONE
  | ifSome (x::xs) f = if f x then SOME(x) else
                       ifSome xs f
(*why this?*)
fun $ (a, f) = f a
end
structure Rand = 
struct
fun Word64toInt (from:Word64.word) = 
    Word64.toInt(Word64.>>((Word64.andb (from,0wx3FFFFFFf00000000)),0w32))
val mult : Word64.word = 0wx5deece66d
val mask : Word64.word = 0wxFFFFFFFFFFFF (* 2^48*)
val c : Word64.word = 0wxb
val seed = ref (0wx123456789abcdef:Word64.word)
               
fun initSeed initVal = let
  val init = Word64.fromInt initVal
  val retVal = Word64.andb ((Word64.xorb (init,mult)),mask)
in (seed:=retVal;retVal) end
                         
fun randInt () = let 
  val next = Word64.andb((Word64.+((Word64.* ((!seed),mult)),c)),mask)
in (seed:=next;Word64toInt(next)) end
fun next bits = let 
  val next = Word64.andb((Word64.+ ((Word64.*((!seed),mult)),c)),mask)
  val retVal = Word64.>>((Word64.andb (next,0wx3FFFFFFf00000000)),(0w48 - bits))
in (seed:=next;retVal) end
  fun randDouble () = let
    val top = Word64.toLargeInt(Word64.+ ((Word64.<<((next 0w26),0w27)),(next 0w27)))
    val bottom = Word64.toLargeInt(Word64.<<(0w1,0w53))
in Real.fromLargeInt(top) / Real.fromLargeInt(bottom) end
end
