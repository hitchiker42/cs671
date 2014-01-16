(* Inifinite lists (sequences)
 * In the documentation, [x1, x2, ..., xn] referes to a list and
 * <x1, x2, ...> referes to a sequence
 *)
signature SEQ = sig

type 'a seq (* a type of sequences; sequences are conceptually infinite *)

(* hd <x1, x2, ...> is x1 *)
val hd : 'a seq -> 'a

(* tl <x1, x2, ...> is <x2, ...> *)
val tl : 'a seq -> 'a seq

(* take(<x1, x2, ...>,k) is [x1, x2, ..., xk] *)
val take : 'a seq * int -> 'a list

(* drop(<x1, x2, ...>,k) is <xk+1, xk+2, ...> *)
val drop : 'a seq * int -> 'a seq

(* append([x1, x2, ..., xn],<y1, y2, ...>) is <x1, x2, ..., xn, y1, y2, ... > *)
val append : 'a list * 'a seq -> 'a seq

(* map F <x1, x2, ...> is <F(x1), F(x2), ...> *)
val map : ('a -> 'b) -> 'a seq -> 'b seq

(* filter F S is the subsequence of S of elements x such that F(x) is true *)
val filter : ('a -> bool) -> 'a seq -> 'a seq

(* find N F S is the first element x of S such that F(x) is true
 * If no such element is found in the fist N values of the sequence,
 * the function returns NONE
 *)
val find : int -> ('a -> bool) -> 'a seq -> 'a option

(* tabulate F is <F(0), F(1), ...> *)
val tabulate : (int -> 'a) -> 'a seq

(* iter F x is <x, F(x), F(F(x)), ... > *)
val iter : ('a -> 'a) -> 'a -> 'a seq

(* iterList F [x1, x2, ..., xn] is
<x1, x2, ..., xn,
 F([x1, x2, ..., xn]),
 F([x2, ..., xn, F([x1, x2, ..., xn])]),
 F([x3, ..., xn, F([x1, x2, ..., xn]), F([x2, ..., xn, F([x1, x2, ..., xn])])]),
 ... >
(in other words, F is repeatedly applied to the previous n elements)
*)
val iterList : ('a list -> 'a) -> 'a list -> 'a seq

(* repeat [x1, x2, ..., xn] is <x1, ..., xn, x1, ..., xn, x1, ...> *)
val repeat : 'a list -> 'a seq

(* merge(<x1, x2, ...>,<y1, y2, ...>) is <x1, y1, x2, y2, ...> *)
val merge : 'a seq * 'a seq -> 'a seq

(* mergeList1 [<x1, x2, ...>, <y1, y2, ...>, ...] is
   <x1, y1, ..., x2, y2, ...>
*)
val mergeList1 : 'a seq list -> 'a seq

(* mergeList2 <[x1, x2, ..., xn1], [y1, y2, ..., yn2], ...> is
   <x1, x2, ..., xn1, y1, y2, ..., yn2, ...>
*)
val mergeList2 : 'a list seq -> 'a seq

(* mergeSeq <s1, s2, ...> is a sequence that contains
 * all the elements of s1 exactly once, all the elements of s2 exactly once, ...
 * (in some order)
 *)
val mergeSeq : 'a seq seq -> 'a seq

(* allLists L is a sequence that contains
 * all the lists that can be made from the elements of L (in some order)
 * Each possible list appears exactly once
 * The function raises Empty if L is empty
*)
val allLists : 'a list -> 'a list seq

(* Naturals is <0, 1, 2, 3, ...>, the sequence of natural numbers *)
val Naturals : int seq

(* upTo N is <0, 1, 2, 3, ..., N, N, N, ...> *)
val upTo : int -> int seq

(* Primes is <2, 3, 5, 7, 11, 13, 17, 19, ...>, the sequence of prime numbers *)
val Primes : int seq

(* randomInt seed is a sequence of pseudo-random integers that uses the algorithm
 * from java.util.Random
 *)
val randomInt : int -> int seq

(* randomReal seed is a sequence of pseudo-random reals that uses the algorithm
 * from java.util.Random
 *)
val randomReal : int -> real seq
end
