(* sig
val curry = fn : ('a * 'b -> 'c) -> 'a -> 'b -> 'c
val expand = fn : ('a -> 'b -> 'c) -> 'a * 'b -> 'c
val eq = fn : ''a -> ''a -> bool
val sub = fn : int -> int -> int
val last = fn : 'a list -> 'a
val most = fn : 'a list -> 'a list
val natList = fn : int -> int list
val sum = fn : int list -> int
val prod = fn : int list -> int
val sum2 = fn : int list list -> int
val isLetter = fn : char -> bool
val toLower = fn : char -> char
val palindrome = fn : string -> bool
val hanoi = fn : int * 'a * 'a * 'a -> ('a * 'a) list
val intpow = fn : int -> int -> int
val powint = fn : int * int -> int
val multiply = fn : (int * int) list -> int
val introot = fn : int -> int
val primes = fn : int -> int list
val filt = fn : ''a list ref -> int
val factor = fn : int -> (int * int) list
val printFact = fn : int -> unit
val isPerm = fn : ''a list * ''a list -> bool
val it = () : unit
*)
fun curry f x y = f(x,y)
fun expand f (x,y) = f x y
(*= is binding eq is testing*)
fun eq x y = x=y
fun sub x y = x-y

(* heres a length function
fun length l = let
      fun len(n, []) = n
        | len (n, [x]) = n + 1
        | len (n,x::xs)=len (n+1,xs)
      in
        len (0, l)
      end
Now, I'm going to use the standard one since its more efficent*)
(*last is already a fxn in the basis library....
to be honest this is the exact same one as in the basis library
there really aren't many other ways to write it*)
fun last [] = raise List.Empty
| last [x] = x
| last (_::x) = last x
(*fun most l = if length l > 1 then List.take(l,List.length l - 1) else l*)
fun most [] = raise List.Empty
  | most (x::[]) = []
  | most (x::y::[]) = x::[]
  | most (x::z) = x::(most z)
(*natList aka seq 0 n && rev*)
fun natList 0 = []
  | natList 1 = 1::[]
  | natList n = let
(*tail recursion I think*)
      fun acc 0 r = r
        | acc n r = acc (n-1) (n::r)
  in acc n [] end
(*this one is kinda silly, there are a million ways to do this*)
fun sum [] = 0
  | sum (x::[]) = x
  | sum (x::xs) = x + sum xs
fun prod [] = 1
  | prod (x::[]) = x
  | prod (x::xs) = x * prod xs
fun sum2 [] = 0
  | sum2 (x::xs) = sum x + sum2 xs
(*Another one thats also a basis function
, the basis function compares the char code
to a set of ascii codes for membership,
though in a different way than this*)
fun isLetter c =
    if (ord c >=0x41 andalso ord c <=0x5A) orelse (ord c >=0x61 andalso ord c <=0x7A)
    then true else false
(* also a basis function*)
fun toLower c =
    if isLetter c then
        if (ord c >=0x41 andalso ord c <=0x5A)
        then chr (ord c + 0x20)
        else c
    else c
fun palindrome s = let
    val ca=explode s
    fun compress [] = []
      | compress (c::[]) = if isLetter c then (c::[]) else []
      | compress (c::ca)  = if isLetter c then ((toLower c)::(compress ca)) else (compress ca)
    val c = compress ca
    (**)
    fun hdtl [x] = [x]
      | hdtl l = ((hd l)::(last l)::[])
    fun test [] = true
      | test (x::[]) = true
      | test (x::y::[]) = (x=y)
      | test l = test (hdtl l) andalso test (most (tl l))
in test c end
(*move n-1 disks to B;
  move disk n to C;
  disk A is now B and disk B is a
  subtract 1 from n and repeat*)
         (*(format t "Move from ~A to ~A.~%" from to))
        (t
         (move (- n 1) from via to)
         (format t "Move from ~A to ~A.~%" from to)
         (move (- n 1) via to from))))*)
fun hanoi (1,A,B,C) = (A,C)::[]
  | hanoi (n,A,B,C) = hanoi((n-1),A,C,B)@(A,C)::hanoi((n-1),B,A,C)
(*I think this works...*)
(*fun muitiply int*int list -> int
for i in int*int list:
  prod*=int**int*)
fun intpow n e =
    let
        fun acc 0 r e = r
          | acc n r e = acc (n-1) (r*e) e
    in acc (e-1) n n end
fun powint (n,e) = expand intpow (n,e)
fun multiply fact =
    let fun acc [] r  = r
          | acc (l::ls) r  = acc ls r*powint(l)
    in acc fact 1 end
(*really simple trial division
[n|n<-ns,rem n p > 0]*)
(*(defun factors (n &aux (lows '()) (highs '()))
  (do ((limit (isqrt n)) (factor 1 (1+ factor)))
      ((= factor limit)
       (when (= n (* limit limit))
         (push limit highs))
       (nreconc lows highs))
    (multiple-value-bind (quotient remainder) (floor n factor)
      (when (zerop remainder)
        (push factor lows)
        (push quotient highs)))))*)*)
(*Input: an integer n > 1

Let A be an array of Boolean values, indexed by integers 2 to n,
initially all set to true.

for i = 2, 3, 4, ..., √n :
  if A[i] is true:
    for j = i2, i2+i, i2+2i, ..., n:
      A[j] := false

Now all i such that A[i] is true are prime.*)
(*(defun move (n from to via)
  (cond ((= n 1)
         (*(format t "Move from ~A to ~A.~%" from to))
        (t
         (move (- n 1) from via to)
         (format t "Move from ~A to ~A.~%" from to)
         (move (- n 1) via to from))))*)*)
fun introot n=Real.floor (Math.sqrt (Real.fromInt n))
fun primes n = let
    (*val filt=fn x => fn n => Int.mod x n = 0*)
    val n = n
    fun trial x (p::[]) = if not(x mod p = 0) then true else false
      | trial x (p::xs) = if not(x mod p = 0) then trial x xs else false
    fun sieve x (p::xs) n= if x > n then (p::xs) else if (trial x (5::3::p::xs)) then
                              sieve (x+2) (x::(p::xs)) n else sieve (x+2) (p::xs) n
in sieve 9 (7::5::3::2::[]) n end
fun filt l = let
    val x = hd (!l)
    val xs = tl (!l)
    val f=fn y => x=y
    val g=(not o f)(*fn y =>not (f y);*)
    val num =List.length (List.filter f (x::xs))
in (l:=List.filter g xs;num) end
fun factor n = let
    val primes = primes (introot n)
    val factors=[]
    (*actual factoring*)
    fun decompose (n:int) (p::xs) (l:int list) = if op*(p,p) > n then
                                               if n>1 then (n::l)
                                               else l
                                           else if (n mod p)=0 then
                                               decompose (op div(n,p)) (p::xs) (p::l)
                                           else decompose n xs l
      | decompose n [] [] = n::[]
    (*formatting of answer
     we have a list factor::factor::factor....factor::[]
     need a list (factor,num)::(factor,num)*)
    fun collect (x:int list ref) (l:(int*int) list) =
        if (!x)=[] then l else
        let
        val q = hd(!x)
        val flt = filt x
    in collect x ((q,flt)::l) end

    val facts = decompose n (rev primes) factors
    val f_list = ref facts
in collect f_list ([]:(int*int) list) end

fun printFact n=let
    val factors=rev (factor n);(*if ans is prime return "x is prime"*)
    fun strpow (x:(int*int)) = if #2(x) = 1 then Int.toString(#1x) else
                               concat (Int.toString(#1x)::"^"::Int.toString(#2x)::[])
    fun acc [x] l = concat ((strpow x)::l)
      | acc (x::xs) l = acc xs (" * "::(strpow x)::l)
    val fact_str=acc factors []
in print (concat ((Int.toString n)::" = "::fact_str::"\n"::[])) end
(*
list factors()
fun factor n primes:
  p=2
  while n>=p*p:
    i=0
    if(n%p=0):
      while(n%p=0):
        fact=(p,i)
        n=n/p
        i++
      list.append(fact)*)
fun isPerm ([],[]) = true
  | isPerm ((z::[]),[]) = false
  | isPerm ([],(z::[])) = false
  | isPerm ((x::xs),y) = let
      fun rot x (l::[]) [] = if l = x then [] else (l::[])
        | rot x (l::[]) z = if l = x then z else []
        | rot x [] z = []
        | rot x (l::ls) z = if l = x then (ls@z)
                            else rot x ls (l::z)
  in isPerm (xs,(rot x y [])) end
