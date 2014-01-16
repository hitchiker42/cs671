(* $Id: tests5bonus.sml 174 2013-03-25 14:43:17Z cs671a $ *)

(* use "/home/csadmin/cs671/tester.sml"; *)
(* use "/Users/charpov/SVN/Programs/SML/Misc/tester.sml";*)

(* 2 *) fun testBonus1 () = isPerm ([1,2,3,2], [2,2,3,1])
(* 2 *) fun testBonus2 () = not (isPerm ([1,2,3,2], [2,1,3,1]))
(* 2 *) fun testBonus3 () = isPerm ([], [])
(* 2 *) fun testBonus4 () = isPerm (["foo"], ["foo"])
(* 1 *) fun testBonus5 () = not (isPerm ([0,0], [0]))
(* 1 *) fun testBonus6 () = let val l = List.tabulate(1000,fn x => x)
                            in isPerm (l, rev l) end

val testsBonus =
    [(2, "isPerm short lists (true)", testBonus1),
     (2, "isPerm short lists (false)", testBonus2),
     (2, "isPerm empty lists", testBonus3),
     (2, "isPerm singletons", testBonus4),
     (1, "isPerm ([0,0],[0])", testBonus5),
     (1, "isPerm long lists", testBonus6)]

(*val _ = Grading.saveToFile 10 (Grading.run testsBonus) "bonus.res"*)
