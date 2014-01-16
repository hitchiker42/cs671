(* Assignment #6: SML 2 *)

local
    val l1 = [3,4,2,6,3,2,10,11,3,3,4,6,5,10]
    val l2 = List.map Int.toString l1
    val l1s = [2,2,3,3,3,3,4,4,5,6,6,10,10,11]
    val l2s = ["6","6","5","4","4","3","3","3","3","2","2","11","10","10"]
in
fun test01 () = mergeSort Int.< l1 = l1s
fun test02 () = mergeSort String.> l2 = l2s
end

fun test03 () = foldl Int.+ 0 (change [4,2,32,5,7,1,33,4,1,6] 41) = 41
fun test04 () = (change [4,2,32,5,7,1,33,4,1,6] 64; false)
    handle CannotChange => true

local
    val l = [2,3,5,7,12,20]
    val sort = mergeSort Int.>
in
fun test05 () = let val x = sort (change l 15) in x=[12,3] orelse x=[7,5,3] end
fun test06 () = (change l 18; false) handle CannotChange => true
fun test07 () = sort (changeBest l 15) = [12,3] handle CannotChange => false
fun test08 () = (changeBest l 18; false) handle CannotChange => true
end

local
  structure S = ListSudoku : SUDOKU
  open S
  val grid1 = parseString "008100700247008000000600308000002500000030000002700000906007000000900216005001400"
  val grid2 = parseString "@@81@@7@@247@@8@@@@@@6@@3@8@@@@@25@@@@@@3@@@@@@27@@@@@9@6@@7@@@@@@9@@216@@5@@14@@"
  val grid3 = parseString ("6 3 8 1 2 5 7 9 4\n"^
                           "2 4 7 3 9 8 6 5 1\n"^
                           "1 5 9 6 7 4 3 2 8\n"^
                           "8 9 3 4 1 2 5 6 7\n"^
                           "4 7 1 5 3 6 9 8 2\n"^
                           "5 6 2 7 8 9 1 4 3\n"^
                           "9 1 6 2 4 7 8 3 5\n"^
                           "7 8 4 9 5 3 2 1 6\n"^
                           "3 2 5 8 6 1 4 7 9\n")
  val sort = mergeSort Int.<
in
fun test09 () = grid1 = grid2
fun test10 () = get (grid1, 1) = 0
fun test11 () = get (grid1, 2) = 8
fun test12 () = get (set (grid1, 2, 5), 2) = 5
fun test13 () = valOf (unsolved grid1) = 0
fun test14 () = sort (possibles (grid1, 0)) = [3,5,6]
fun test15 () = not (isSome (unsolved grid3))
fun test16 () = possibles (grid3, 0) = [6]
fun test17 () = valid grid3
fun test18 () = not (valid grid1)
fun test19 () = not (valid (set (grid3, 1, 1)))
fun test20 () = sudoku grid1 = grid3 (* grid1's solution is unique *)
end

val allTests =
    [
     test01,test02,test03,test04,test05,test06,test07,test08,test09,test10,
     test11,test12,test13,test14,test15,test16,test17,test18,test19,test20
    ]

val result = List.all (fn f => f()) allTests
