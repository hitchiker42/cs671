
structure Seq : SEQ = struct
val valOf =Option.valOf
datatype 'a seq = Cons of 'a option * (unit -> 'a seq)
(*such that hd nseq = (!ref) and (unit->'a seqn) is an implict fxn of ref*)
fun hd (Cons(NONE, f)) = hd (f())
  | hd (Cons(SOME x, _)) = x
val Car = hd
(*KLUDGE:deal with empty seq*)
fun testEmpty (Cons(NONE,f)) = let
  fun test (Cons(NONE,f)) 0 = false
    | test (Cons(NONE,f)) n = test(f()) (n-1)
    | test(Cons(SOME(x),f)) n = true
in test (Cons(NONE,f)) end
fun tl (Cons(NONE,f)) = let
  fun limit (Cons(NONE,g)) 0 = g()
     |limit (Cons(NONE,g)) n = limit(g()) (n - 1)
     |limit (Cons(SOME(x),g)) n = g()
in limit (Cons(NONE,f)) 10000  end
  | tl (Cons(SOME x,f)) = f()
fun Cdr(Cons(NONE,f)) = let
  fun limit (Cons(NONE,g)) 0 = g
     |limit (Cons(NONE,g)) n = limit(g()) (n - 1)
     |limit (Cons(SOME(x),g)) n = g
in limit (Cons(NONE,f)) 10000  end
  | Cdr(Cons(SOME(x),f)) = f
fun next (Cons(_,f)) = f()
fun first (Cons(NONE,f)) = NONE
   |first (Cons(SOME(x),f)) = SOME(x)
fun take (sequ,n) = let
  fun acc se 0 x = rev x
     |acc se n x = acc  (tl se) (n - 1) ((Car se)::x)
in acc sequ n [] end
fun drop (sequ,n)= let
  fun acc (x,1) = tl(x)
     |acc (x,m) = acc ((tl x),(m - 1))
in acc(sequ,n) end
fun append (lst,(Cons(opt,f))) = let
  fun h () = Cons(opt,f)
  fun g [] = h ()
    | g (x::xs) = Cons(SOME(x),(to_unit (g,xs)))
in Cons(NONE,to_unit (g,lst)) end
fun map g sequ = let
  val sfst = if Option.isSome(first sequ) then
               SOME(g(hd(sequ))) else NONE
  fun h s = let
    val a = hd(s)
  in Cons(SOME(g(a)),to_unit(h,next(s)))
     handle Option => h(next(s))
           |Subscript => Cons(NONE,to_unit(h,next(s))) end
in Cons(sfst,to_unit(h,(next(sequ)))) end
fun filter g sequ = let
  fun h s = let
    val temp = hd s
  in if (g temp) then Cons(SOME(temp),to_unit(h,(tl s)))
     else Cons(NONE,to_unit(h,(tl s))) end
in Cons(NONE,to_unit(h,sequ)) end
fun find n f sequ = let
  val cnt = ref n
  fun check x = if (!cnt)=0 then NONE
                else if f(Car(x)) then SOME(Car(x))
                else (cnt-=1;check (tl x))
in check sequ end
fun iter f x = let
  fun g z = Cons(SOME(z),(to_unit(g,(f(z)))))
in g x end
fun iterList f initl = let
  fun g lst = Cons(SOME(f(lst)),to_unit(g,((cdr lst)@([f(lst)]))))
in append(initl,g initl)end
fun repeat [] = raise Empty
  | repeat x = let
    val arr = Array.fromList x
    val len = ((Array.length arr) - 1)
    fun f n = if n = len then Cons(SOME(Array.sub(arr,n)),to_unit(f,0))
              else Cons(SOME(Array.sub(arr,n)),to_unit(f,(n + 1)))
  in Cons(NONE,to_unit(f,0)) end
fun merge (seqA,seqB) = let
  fun h (seq1,seq2,true) =
      Cons (SOME(Car(seq1)),to_unit(h,((Cdr(seq1)()),seq2,false)))
    | h (seq1,seq2,false) =
      Cons (SOME(Car (seq2)),to_unit(h,(seq1,(Cdr(seq2)()),true)))
in Cons(NONE,to_unit(h,(seqA,seqB,true))) end
fun mergeList1 seql = let
  val arr = Array.fromList seql
  val len = ((Array.length arr) - 1)
  val get = Array.sub
  val set = Array.update
  fun f n = if n = len then let
              val temp = Cons(first(get(arr,n)),to_unit (f,0))
            in (set(arr,n,tl(get(arr,n)));temp) end
            else let
              val temp = Cons(first(get(arr,n)),to_unit (f,(n+1)))
            in (set(arr,n,tl(get(arr,n)));temp) end
in Cons(NONE,to_unit(f,0)) end
fun mergeList2 seql = let
  fun h ((x::[]),sequ) = Cons(SOME(x),to_unit(h,(hd(sequ),tl(sequ))))
    | h ((x::xs),sequ) = Cons(SOME(x),to_unit(h,(xs,sequ)))
    | h ([],sequ) = Cons(NONE,to_unit(h,(hd(sequ),tl(sequ))))
in Cons(NONE,to_unit(h,(hd(seql),tl(seql)))) end

fun mergeSeq sequ = let
  fun emptySeq () = Cons(NONE,emptySeq)
  fun f ((s:'a seq seq),l,l2,cnt1,cnt2) =
      if cnt1=0 
      then f((tl s),(hd(s)::l2),[],cnt2,(cnt2+1))
             else
               Cons(SOME(hd(car(l))),to_unit(f,(s,cdr(l),((tl(car l))::l2),(cnt1 -1),cnt2)))
in f(sequ,[],[],0,0) end
fun allLists lst = let
  val len = List.length lst
  fun f (lst1,lst2,cnt,cnt2) = if cnt = len then let 
                             fun nxt 0 l = l
                               | nxt n l = nxt (n-1) (List.nth(lst,(cnt2 mod (len -1)))::l)
                              in f (lst,(nxt (cnt2 +1) []),0,(cnt2 +1)) end
                              else Cons(SOME(lst2),to_unit(f,((List.drop(lst1,(cnt)),((List.nth(lst1,cnt))::lst2),(cnt+1),cnt2))))
in f(lst,[car(lst)],0,0) end
fun upTo n = let
  val final = n
  fun f 0 = Cons(SOME(final),to_unit(f,0))
    | f m = Cons(SOME(m),to_unit(f,(m - 1)))
in Cons(NONE,to_unit(f,n)) end
(*Note about these 2, should you actully look at this code, Rand works
exactly like the java code, so a call to Rand.randomInt will change the 
value of the next call to Rand.randomReal*)
fun randomInt initVal = let 
  val _ = Rand.initSeed initVal
  fun f () = Cons(SOME(Rand.randInt()),f)
in f() end
fun randomReal initVal = let 
  val _ = Rand.initSeed initVal
  fun f () = Cons(SOME(Rand.randDouble()),f)
in f() end
fun nat n = Cons(SOME(n),to_unit(nat,(n +1)))
val Naturals = Cons(SOME(0),to_unit(nat,1))
fun tabulate f = map f Naturals
val P = ref [2,3,5,7,11,13]
fun primes n = 
    if MiscList.contains (!P) n then SOME(n)
    else let
      (*val filt=fn x => fn n => Int.mod x n = 0*)
      val curP = rev(!P)
      fun trial (x:int) (p::[]) = if not(x mod p = 0) then
                         ((P::=x);true) else false
        | trial x (p::xs) = if not(x mod p = 0)
                            then trial x xs else false
    in if trial n curP then SOME(n) else NONE end
fun pseq n = Cons(primes n,to_unit(pseq,(n+1)))
val Primes = Cons(primes 2,to_unit(pseq,3))
end
(*
  a #Define mult 0x5deece66d
  c #Define addend 0xb
  #Define mask 0xfffffffff
  // << = left shift
  long* seed = (seed xor mult) & mask
  long genseed (long* val) {
     *val = ((&val)*mutl+addend) & mask;
     return &val;  
  }
  int next(int bits){
    long nxt = genseed(seed);
    //  >>> = unsinged right shift
    return (nxt >>> (48 -bits))
  }
  int nextInt() {
    return next(32);
  }
  double nextDouble(){
    return (((long)(next(26)) << 27) + next(27)) / (double)(1 << 53);
  }

The generator is defined by the [[recurrence relation]]:

: <math>X_{n+1} \equiv \left( a X_n + c \right)~~\pmod{m}</math>

where <math>X</math> is the [[sequence]] of pseudorandom values, and

: <math> m,\, 0<m </math> — the "[[modulo operation|modulus]]"
: <math> a,\,0 < a < m</math> — the "multiplier"
: <math> c,\,0 \le c < m</math> — the "increment"
: <math> X_0,\,0 \le X_0 < m</math> — the "seed" or "start value"
a = 25214903917
   c = 11
   m = 2 ^ 48
   return 32 most sig bits for a random int
  long* seed = (seed xor mult) & mask
  long genseed (long* val) {
     *val = ((&val)*mutl+addend) & mask;
     return &val;  
  }
  int next(int bits){
    long nxt = genseed(seed);
    //  >>> = unsinged right shift
    return (nxt >>> (48 -bits))*)
