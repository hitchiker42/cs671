(* $Id: sampleTests5.sml 165 2013-03-07 20:46:55Z cs671a $ *)
(* Assignment #5: SML 1, sample tests *)

(* helper function to test stdout output *)
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

fun test3 () = last [1,2,3,4,5] = 5
fun test4 () = last ["foo"] = "foo"

fun test5 () = natList 10 = [1,2,3,4,5,6,7,8,9,10]
fun test7 () = null (natList 0)

fun test10 () = sum (natList 1000) = 500500

fun test12 () = prod [1,2,3,4,5,6,7,8,9,10] = 3628800

fun test15 () = sum2 [[1,2],[3,4]] = 10

fun test20 () = isLetter #"m" andalso isLetter #"M"
fun test21 () = not (isLetter #"2" orelse isLetter #"!")

fun test22 () = toLower #"M" = #"m"
fun test23 () = toLower #"$" = #"$"

fun test24 () = palindrome "Madam, in Eden, I'm Adam."
fun test25 () = not (palindrome "Abracadabra!")

fun test28 () = length (hanoi (10,1,2,3)) = 1023

fun test30 () = factor 123456789 = [(3,2),(3607,1),(3803,1)]

fun test33 () = 123456789 = multiply [(3,2),(3607,1),(3803,1)]

fun test37 () = getString printFact 1776 = "1776 = 2^4 * 3 * 37\n"
