(* $Id: tests5.sml 174 2013-03-25 14:43:17Z cs671a $ *)
(* Assignment #5: SML 1 *)

(* use "/home/csadmin/cs671/tester.sml"; *)
(* use "/Users/charpov/SVN/Programs/SML/Misc/tester.sml"; *)

(* 3 *) fun test1 () = (last []; false) handle _ => true
(* 3 *) fun test2 () = last [tl, tl o tl] [1,2,3] = [3]
(* 2 *) fun test3 () = last [1,2,3,4,5] = 5
(* 2 *) fun test4 () = last ["foo"] = "foo"

(* 4 *) fun test5 () = natList 10 = [1,2,3,4,5,6,7,8,9,10]
(* 3 *) fun test6 () = let val l = natList 10000 in
                length l = 10000 andalso hd l = 1 andalso List.last l = 10000
            end
(* 3 *) fun test7 () = null (natList 0)

(* 2 *) fun test8 () = sum [1,2,3,4,5,6,7,8,9,10] = 55
(* 1 *) fun test9 () = sum [] = 0
(* 2 *) fun test10 () = sum (natList 1000) = 500500

(* 1 *) fun test11 () = prod [] = 1
(* 2 *) fun test12 () = prod [1,2,3,4,5,6,7,8,9,10] = 3628800
(* 1 *) fun test13 () = prod (List.tabulate (29,fn _ => 2)) = 536870912
(* 1 *) fun test14 () = prod (List.tabulate (100000, fn 0 => 1789 | _ => 1))
                        = 1789

(* 2 *) fun test15 () = sum2 [[1,2],[3,4]] = 10
(* 2 *) fun test16 () = sum2 [] = 0
(* 2 *) fun test17 () = sum2 [[]] = 0
(* 2 *) fun test18 () = sum2 (List.tabulate (10000, fn _ => [])) = 0
(* 2 *) fun test19 () = sum2 (List.tabulate (10, natList)) = 165

(* 3 *) fun test20 () = isLetter #"m" andalso isLetter #"M"
(* 2 *) fun test21 () = not (isLetter #"2" orelse isLetter #"!")

(* 3 *) fun test22 () = toLower #"M" = #"m"
(* 2 *) fun test23 () = toLower #"$" = #"$"

(* 3 *) fun test24 () = palindrome "Madam, in Eden, I'm Adam."
(* 3 *) fun test25 () = not (palindrome "Abracadabra!")
(* 2 *) fun test26 () = palindrome "@"
(* 2 *) fun test27 () = palindrome "#X%"

(* 5 *) fun test28 () = length (hanoi (10,1,2,3)) = 1023
(* 5 *) fun test29 () = hanoi (3,"A","B","C") = [("A","C"),("A","B"),("C","B"),
                                       ("A","C"),("B","A"),("B","C"),("A","C")]

(* 3 *) fun test30 () = factor 123456789 = [(3,2),(3607,1),(3803,1)]
(* 3 *) fun test31 () = factor 123456 = [(2,6),(3,1),(643,1)]
(* 3 *) fun test32 () = factor 536870912 = [(2,29)]

(* 3 *) fun test33 () = 123456789 = multiply [(3,2),(3607,1),(3803,1)]
(* 3 *) fun test34 () = 123456 = multiply [(2,6),(3,1),(643,1)]
(* 3 *) fun test35 () = 536870912 = multiply [(2,29)]

(* 2 *) fun test36 () = let fun check 1 = true
                   | check n = multiply (factor n) = n andalso check (n-1)
             in
                 check 100000
             end

(* 3 *) fun test37 () = printFact 1776  (* "1776 = 2^4 * 3 * 37\n" *)
(* 3 *) fun test38 () = printFact 1789  (* "1789 is prime\n" *)
(* 2 *) fun test39 () = printFact 1024  (* "1024 = 2^10\n" *)
(* 2 *) fun test40 () = printFact 27    (* "27 = 3^3\n" *)

local
    val s = ref ""
    fun pr x = s := !s ^ x
    val print = SMLofNJ.Internals.prHook
    val truePrint = !print
in
fun getString f x =
    (s := "";
     print := pr;
     f x;
     print := truePrint;
     !s)
end

val allTests =
    [(3, "last on empty list", test1),
     (3, "last on list of functions", test2),
     (2, "last on list of numbers", test3),
     (2, "last on short list of strings", test4),

     (4, "natList 10", test5),
     (3, "natList 10000", test6),
     (3, "natList 0", test7),

     (2, "sum on short list", test8),
     (1, "sum on empty list", test9),
     (2, "sum on long list", test10),

     (1, "prod on empty list", test11),
     (2, "prod on short list", test12),
     (1, "prod on long list", test13),
     (1, "prod on huge list", test14),

     (2, "sum2 on short list", test15),
     (2, "sum2 on empty list", test16),
     (2, "sum2 [[]]", test17),
     (2, "sum2 on huge list", test18),
     (2, "sum2 on medium list", test19),

     (3, "isLetter on letters", test20),
     (2, "isLetter on non-letters", test21),

     (3, "toLower on a letter", test22),
     (2, "toLower on a non-letter", test23),

     (3, "palindrome \"Madam, in Eden, I'm Adam.\"", test24),
     (3, "palindrome \"Abracadabra!\"", test25),
     (2, "palindrom \"@\"", test26),
     (2, "palindrome \"#X%\"", test27),

     (5, "hanoi, 10 discs", test28),
     (5, "hanoi, 3 discs", test29),

     (3, "factor 123456789", test30),
     (3, "factor 123456", test31),
     (3, "factor 536870912", test32),

     (3, "multiply of the factors of 123456789", test33),
     (3, "multiply of the factors of 123456", test34),
     (3, "multiply of the factors of 536870912", test35),
     (2, "factor then multiply of all number 1..10000", test36),

     (3, "printFact 1776",
         fn () => getString printFact 1776 = "1776 = 2^4 * 3 * 37\n"),
     (3, "printFact 1789",
         fn () => getString printFact 1789 = "1789 is prime\n"),
     (2, "printFact 1024",
         fn () => getString printFact 1024 = "1024 = 2^10\n"),
     (2, "printFact 27",
         fn () => getString printFact 27 = "27 = 3^3\n")
    ]

(*val _ = Grading.saveToFile 100 (Grading.run allTests) "tests.res"*)
