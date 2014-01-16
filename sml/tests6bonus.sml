(* $Id: tests6bonus.sml 179 2013-04-04 17:12:24Z cs671a $ *)
(* Assignment #6: SML 2 *)

local
  fun allChg _ 0 = [[]]
    | allChg [] _ = []
    | allChg (x::l) n = allChg l n @
                        (if x>n then []
                         else map (fn s => x::s) (allChg l (n-x)))

  fun uniq (x :: (l as y :: _)) = if x=y then uniq l
                                  else if length x = length y then x :: uniq l
                                  else [x]
    | uniq l = l : int list list

  fun cmp2 ([],[]) = true
    | cmp2 (x::l,y::t) = x>y orelse x=y andalso cmp2 (l,t)

  fun cmp (a,b) = let val (x,y) = (length a, length b) in
                    x>y orelse x=y andalso cmp2 (a,b)
                  end

  fun sort2 l = ListMergeSort.sort cmp l
in
fun allChangeBest l n = uniq (sort2 (map sort (allChg l n)))
end

local
    val l = [2,3,5,7,12,20]
in
fun test01 () = sort (changeBest l 15) = [12,3]
fun test02 () = (changeBest l 18; false) handle CannotChange => true
end

local
  fun prList l = List.app (fn s => print ((Int.toString s)^" ")) l
  fun test l t =
      let val a = allChangeBest l t
      in
        case
          let val s = sort (changeBest l t)
          in List.exists (fn x => x=s) a end
          handle CannotChange => null a
         of true => true
          | false =>
            (print (Int.toString t);
             print " with ";
             prList l;
             print "(";
             print (Int.toString (length a));
             print " solution(s)): FAILED\n";
             false)
      end
  fun loop n t =
      let fun iter 0 = true
            | iter i = test (makeList t n) (Random.randRange (1,t) rd)
                       andalso iter (i-1)
      in
        iter 100
      end
in
fun test03 () = loop 10 100
fun test04 () = loop 20 100
fun test05 () = loop 30 200
fun test06 () = loop 50 500
end

val testsBonus = [
    (3, "possible changeBest on a short list", test01),
    (3, "impossible changeBest on a short list", test02),
    (5, "changeBest on 100 random problems of length 10", test03),
    (4, "changeBest on 100 random problems of length 20", test04),
    (3, "changeBest on 100 random problems of length 30", test05),
    (2, "changeBest on 100 random problems of length 50", test06)
]

(*val resultBonus = List.all (fn (_,_,f) => f()) allTests*)
