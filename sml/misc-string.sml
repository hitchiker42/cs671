structure MiscString = struct
val _ = use "misc-list.sml"
open MiscList
fun strip str =let
    val chars = explode str
    val f = (not o Char.isSpace)
in List.filter f chars end
fun itemize str = let
  val chars = ref (explode str)
  val f = (not o Char.isSpace)
  val results = ref ([]:string list)
  val temp = ref ([]:char list)
  fun q () = let
    val c = des_car chars
  in if (!chars)=[] then if (f c) then
                             rev ((implode (rev(c::(!temp)))::(!results)))
                                  else rev ((implode (rev(!temp)))::(!results))
     else if (f c) then (temp::=c;q ())
     else if (!temp = []) then q () else
     (results::=(implode (rev (!temp)));temp:=[];q()) end
in q () end
fun print_list (l:string list)=let
    val strings=ref l
in while not ((!strings)=[]) do
         print (concat ((des_car(strings))::"\n"::[])) end
fun search chr str = MiscList.contains (explode str) chr
fun find chr str = let
  val chars = rev (explode str)
  val len = (List.length chars -1)
  fun loc (x::xs) n = if x = chr then n else loc xs (n + 1)
in len - (loc chars 0) end
fun $ (a, f) = f a
fun id x = x
structure Printf =
   struct
      fun fprintf out =
         Fold.fold ((out, id), fn (_, f) => f (fn p => p ()) ignore)

      val printf = fn z => fprintf TextIO.stdOut z

      fun one ((out, f), make) =
         (out, fn r =>
          f (fn p =>
             make (fn s =>
                   r (fn () => (p (); TextIO.output (out, s))))))

      val ` =
         fn z => Fold.step1 (fn (s, x) => one (x, fn f => f s)) z

      fun spec to = Fold.step0 (fn x => one (x, fn f => f o to))

      val B = fn z => spec Bool.toString z
      val I = fn z => spec Int.toString z
      val R = fn z => spec Real.toString z
   end
end
