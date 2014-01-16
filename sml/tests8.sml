use "sampleTests8.sml";

local
  open Seq
in
fun test19 () = let val s = append([], N)
                in get 0 s = 0 andalso get 1776 s = 1776 end

fun test20 () = let fun f 0 = 42 | f _ = raise Fail "boom"
                  val s = map f N
                in
                  get 0 s = 42 andalso
                  ((get 10 s; false) handle Fail _ => true)
                end

fun test21 () = get 0 (filter (fn x => x < 1) N) = 0

fun test22 () = let val s = tabulate (fn x => tabulate (fn y => x))
                in get 2013 (hd s) = 0 andalso hd (get 2013 s) = 2013 end

fun test23 () = let val s = iter (fn x => () :: x) []
                in length (get 2012 s) = 2012 andalso get 1 s = [()] end

fun test24 () = let val s = iterList List.hd [1,2,3]
                in take (s, 10) = [1,2,3,1,2,3,1,2,3,1] end

fun test25 () = let fun f [x] = if x mod 2 = 0 then x div 2 else 3*x+1
                in get 106 (iterList f [31]) = 1 end

fun test26 () = get 2013 (repeat ["X"]) = "X"

fun test27 () = let val x = iter (fn _ => #"A") #"A"
                  val y = tabulate (fn _ => #"B")
                  val s = merge (x,y)
                in take (s, 10) = explode "ABABABABAB" end

fun test28 () = let fun m x = tabulate (fn _ => x)
                  val s = mergeList1 (List.tabulate(100, m))
                  val l = List.tabulate(2013, fn x => x mod 100)
                in take(s, 2013) = l end

fun test29 () = let fun f x = if x mod 2013 = 0 then [#"A",#"B",#"C"] else []
                  val s = mergeList2 (tabulate f)
                in take(s, 10) = explode "ABCABCABCA" end

fun test30 () = let fun f x = tabulate (fn y => (x,y))
                  val s = mergeSeq (tabulate f)
                  fun fdp s y = findPos (fn x : int * int => x=y) s
                  fun fdn s y = findNeg 1000 (fn x : int * int => x=y) s
                  val x = hd s
                  val y = hd (tl s)
                in fdp s (3,14) andalso
                   fdp s (0,0) andalso
                   fdn (tl s) x andalso
                   fdn (tl (tl s)) y
                end

fun test31 () = let val s = upTo 2013
                in take (drop (s, 2010), 5) = [2010, 2011, 2012, 2013, 2013] end

fun test32 () = take(drop (Primes, 1000), 10)
                = [7927,7933,7937,7949,7951,7963,7993,8009,8011,8017]

fun test33 () = take(drop(randomInt 421, 1000), 10)
                = [~446456157,~667882199,861369777,33830573,~287331603,
                   ~713504918,149275421,~417988081,174847463,445085017]

fun test34 () = let fun cmp [] [] = true
                      | cmp (x::l) (y::t) = Real.abs(x-y) < 1e~5 andalso cmp l t
                in cmp (take(drop(randomReal 421, 1000), 10))
                       [0.928955, 0.173122, 0.502064, 0.734257, 0.095789,
                        0.686121, 0.283522, 0.026618, 0.541779, 0.504515]
                end

fun testBonus2 () = let val s = allLists [1,0]
                      fun fdp s y = findPos (fn x : int list => x=y) s
                      fun fdn s y = findNeg 1000 (fn x : int list => x=y) s
                      val x = hd s
                      val y = hd (tl s)
                    in fdp s [1,1,0] andalso
                       fdp s [] andalso
                       fdp s [1,1,1] andalso
                       fdp s [0,0] andalso
                       fdn (tl s) x andalso
                       fdn (tl (tl s)) y
                    end
end

val allTests = [
    (2, "",test01), (2, "",test02), (2, "",test03), (2, "",test04), (2, "",test05),
    (2, "",test06), (2, "",test07), (2, "",test08), (2, "",test09), (2, "",test10),
    (2, "",test11), (3, "",test12), (3, "",test13), (3, "",test14), (3, "",test15),
    (3, "",test16), (3, "",test17), (3, "",test18), (1, "",test19), (1, "",test20),
    (1, "",test21), (2, "",test22), (2, "",test23), (2, "",test24), (1, "",test25),
    (1, "",test26), (2, "",test27), (2, "",test28), (2, "",test29), (2, "",test30),
    (2, "",test31), (3, "",test32), (3, "",test33), (3, "",test34),
    (1, "",term01), (1, "",term02), (1, "",term03), (1, "",term04), (1, "",term05),
    (1, "",term06), (1, "",term07), (1, "",term08), (1, "",term09), (1, "",term10),
    (1, "",term11), (1, "",term12), (1, "",term13), (1, "",term14), (1, "",term15),
    (1, "",term16), (1, "",term17), (1, "",term18), (1, "",term19), (1, "",term20),
    (1, "",term21), (1, "",term22), (1, "",term23), (1, "",term24), (1, "",term25),
    (1, "",term26), (1, "",term27)]

val testsBonus = [ (5, "",testBonus1), (5, "",testBonus2)]

(*val OK = List.all (fn (_, t) => t()) allTests
val points = foldl (fn ((v,t),p) => if t() then p+v else p) 0 allTests*)
