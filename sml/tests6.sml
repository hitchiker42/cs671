(* $Id: tests6.sml 184 2013-04-15 23:10:18Z cs671a $ *)
(* Assignment #6: SML 2 *)

val rd = Random.rand(20,13);
fun makeList m n = List.tabulate (n, fn _ => Random.randRange (1,m) rd)
fun sort l = ListMergeSort.sort Int.< l

local
  val l1 = [3,4,2,6,3,2,10,11,3,3,4,6,5,10]
  val l2 = List.map Int.toString l1
  val l1s = [2,2,3,3,3,3,4,4,5,6,6,10,10,11]
  val l2s = ["6","6","5","4","4","3","3","3","3","2","2","11","10","10"]
in
fun test01 () = mergeSort Int.< l1 = l1s
fun test02 () = mergeSort String.> l2 = l2s
fun test03 () = let val l = makeList 100 100 in mergeSort Int.> l = sort l end
fun test04 () = let val l = makeList 10000 10000 in mergeSort Int.> l = sort l end
fun test05 () = let val l = makeList 100 10000 in mergeSort Int.> l = sort l end
fun test06 () = quickSort Int.< l1 = l1s
fun test07 () = quickSort String.> l2 = l2s
fun test08 () = let val l = makeList 100 100 in quickSort Int.> l = sort l end
fun test09 () = let val l = makeList 10000 10000 in quickSort Int.> l = sort l end
fun test10 () = let val l = makeList 100 10000 in quickSort Int.> l = sort l end
fun test11 () = mergeSort String.> [] = []
fun test12 () = mergeSort Int.> [42] = [42]
fun test13 () = quickSort String.> [] = []
fun test14 () = quickSort Int.> [42] = [42]
end

fun test15 () = foldl Int.+ 0 (change [4,2,32,5,7,1,33,4,1,6] 41) = 41
fun test16 () = (change [4,2,32,5,7,1,33,4,1,6] 64; false)
    handle CannotChange => true

local
    val l = [2,3,5,7,12,20]
in
fun test17 () = let val x = sort (change l 15) in x=[12,3] orelse x=[7,5,3] end
fun test18 () = (change l 18; false) handle CannotChange => true
end

local
  fun prList l = List.app (fn s => print ((Int.toString s)^" ")) l
  fun test l t =
      let val a = allChange l t
      in
        case
          let val s = sort (change l t)
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
fun test21 () = loop 10 100
fun test22 () = loop 20 100
fun test23 () = loop 30 200
fun test24 () = loop 50 500
end

local
  structure S = ListSudoku : SUDOKU
  open S

  fun grid1() = parseString "008100700247008000000600308000002500000030000002700000906007000000900216005001400"
  fun grid1'() = parseString "@@81@@7@@247@@8@@@@@@6@@3@8@@@@@25@@@@@@3@@@@@@27@@@@@9@6@@7@@@@@@9@@216@@5@@14@@"
  fun grid1s() = parseString ("6 3 8 1 2 5 7 9 4\n"^
                           "2 4 7 3 9 8 6 5 1\n"^
                           "1 5 9 6 7 4 3 2 8\n"^
                           "8 9 3 4 1 2 5 6 7\n"^
                           "4 7 1 5 3 6 9 8 2\n"^
                           "5 6 2 7 8 9 1 4 3\n"^
                           "9 1 6 2 4 7 8 3 5\n"^
                           "7 8 4 9 5 3 2 1 6\n"^
                           "3 2 5 8 6 1 4 7 9\n")

  fun grid2() = parseString "070050000026000107040000020000030008000007601007901402604093000000005000809000004"
  fun grid2s() = parseString "178652943526349187943178526261534798495287631387961452614893275732415869859726314"

  fun grid3() = parseString  "043080250600000000000001094900004070000608000010200003820500000000000005034090710"
  fun grid3s() = parseString "143986257679425381285731694962354178357618942418279563821567439796143825534892716"
  val sort = ListMergeSort.sort Int.>
in
fun testSudoku () =
    let val l = [1,4,3,9,8,6,2,5,7,6,7,9,4,2,5,3,8,1,2,8,5,7,3,1,6,9,4,9,6,2,3,5,4,1,7,8,3,5,7,6,1,8,9,4,2,4,1,8,2,7,9,5,6,3,8,2,1,5,6,7,4,3,9,7,9,6,1,4,3,8,2,5,5,3,4,8,9,2,7,1,6]
      val g = grid3s()
      fun iter 81 _ = true
        | iter i (x :: t) = x = get (g,i) andalso iter (i+1) t
    in
      iter 0 l
    end

fun test19 () = testSudoku() andalso grid1() = grid1'()
fun test25 () = testSudoku() andalso get (grid1(), 1) = 0
fun test26 () = testSudoku() andalso get (grid1(), 2) = 8
fun test27 () = testSudoku() andalso get (set (grid1(), 2, 5), 2) = 5
fun test28 () = testSudoku() andalso valOf (unsolved (grid1())) = 0
fun test29 () = testSudoku() andalso sort (possibles (grid1(), 0)) = [3,5,6]
fun test30 () = testSudoku() andalso not (isSome (unsolved (grid1s())))
fun test31 () = testSudoku() andalso possibles (grid1s(), 0) = [6]
fun test32 () = testSudoku() andalso valid (grid1s())
fun test33 () = testSudoku() andalso not (valid (grid1()))
fun test34 () = testSudoku() andalso not (valid (set (grid1s(), 1, 1)))
fun test35 () = testSudoku() andalso sudoku (grid1()) = grid1s() (* grid1's solution is unique *)
fun test36 () = testSudoku() andalso sudoku (grid2()) = grid2s() (* grid2's solution is unique *)
fun test37 () = testSudoku() andalso sudoku (grid3()) = grid3s() (* grid3's solution is unique *)
end

val allTests = [
    (1, "mergeSort on a short list of ints", test01),
    (1, "mergeSort on a short list of strings", test02),
    (2, "mergeSort on a list of 100 ints", test03),
    (2, "mergeSort on a list of 10000 ints with few duplicates", test04),
    (2, "mergeSort on a list of 10000 ints with many duplicates", test05),

    (1, "quickSort on a short list of ints", test06),
    (1, "quickSort on a short list of strings", test07),
    (2, "quickSort on a list of 100 ints", test08),
    (2, "quickSort on a list of 10000 ints with few duplicates", test09),
    (2, "quickSort on a list of 10000 ints with many duplicates", test10),

    (1, "mergeSort on an empty list", test11),
    (1, "mergeSort on a singleton list", test12),
    (1, "quickSort on an empty list", test13),
    (1, "quickSort on a singleton list", test14),

    (3, "possible change on a short list", test15),
    (3, "impossible change on a short list", test16),
    (5, "possible change on a short list", test17),
    (5, "impossible change on a short list", test18),
    (5, "change on 100 random problems of length 10", test21),
    (4, "change on 100 random problems of length 20", test22),
    (3, "change on 100 random problems of length 30", test23),
    (2, "change on 100 random problems of length 50", test24),

    (5, "sudoku: parseString", test19),
    (2, "sudoku: get", test25),
    (2, "sudoku: get", test26),
    (3, "sudoku: get and set", test27),
    (3, "sudoku: unsolved", test28),
    (3, "sudoku: unsolved", test30),
    (4, "sudoku: possibles", test29),
    (4, "sudoku: possibles", test31),
    (3, "sudoku: valid", test32),
    (3, "sudoku: valid", test33),
    (3, "sudoku: valid", test34),
    (5, "sudoku: sudoku", test35),
    (5, "sudoku: sudoku", test36),
    (5, "sudoku: sudoku", test37)
]

(*val result = List.all (fn (_,_,f) => f()) allTests*)
