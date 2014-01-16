val load = use
val _ = load "load.sml"
val _ = load "sigs.sml"
functor Expression (S:sig val specials:string end):EXPRESSION =
  struct
  datatype 'a expr = Cex of 'a
                   | Vex of string
                   | Bex of string*'a expr*'a expr
  val specials = explode S.specials
  fun parens c = (Char.ord c = 0x28) || (Char.ord c = 0x29)
  exception Parse of string
  fun special (c:char) = (MiscList.contains specials c)
  fun isIdentifier (f:(string->'a option)) str =
      if Option.isSome (f str) then false
      else let
        val tmp = ref (explode str)
        val c = ncar tmp
        val chars = (!tmp)
        fun test [] b = true
           |test (a::[]) b = ((special a) = (special b))
          | test (a::sa) b = ((special a) = (special b)) && (test sa a)
      in test chars c end
  fun parse mkLit s = (* operators are all left-associative *)
      let fun const t = case mkLit t of
                            SOME x => Cex x
                          | NONE => Vex t
          fun p2 (p as (_,"("::_)) = p
            | p2 (p as (_,")"::_)) = p
            | p2 (p as (_,[])) = p
            | p2 (p as (e1,t::tokens)) =
              case mkLit t of
                  NONE => let val (e2,tokens2) = p1 tokens
                          in
                              p2 (Bex (t,e1,e2),tokens2)
                          end
                | _ => p (* literals cannot be operators *)
          and  p1 ("("::tokens) = (case p2 (p1 tokens) of
                                       (e,")" :: tokens2) => (e, tokens2)
                                     | _ =>
                                       raise Parse "Missing closing parenthesis")
             | p1 (")"::_) = raise Parse "Unexpected token: )"
             | p1 [] = raise Parse "Identifier/literal expected"
             | p1 (t::tokens) = (const t, tokens)
      in
          case p2 (p1 (tokenize s)) of
              (e,[]) => e
            | (e,")"::[]) => e
            | (_,t::_) => raise Parse ("Unexpected token: "^t)
      end
  (*val tokenize : string -> string list
   *other fxns defined in expression sig*)
  and tokenize str = let
      val chars = ref (explode str)
      val results = ref ([]:string list)
      val temp = ref ([]:char list)
      val new = ref (pop chars)
      val old = ref #"b"
      val _ = if Char.isSpace (!new) then () else (temp ::=(!new))
      fun q () = let
        val _ =old:=(!new)
        val _ =new:= pop chars
      in if (!chars)=[] then if Char.isSpace (!new)
                          then if (!temp) = [] then rev (!results)
                               else rev ((implode(rev (!temp)))::(!results))
                          else if special (!new) != special (!old) || (parens (!new) != parens (!old))
                             then rev ((Char.toString (!new))::(implode (rev ((!temp))))::(!results))
                             else rev ((implode(rev ((!new)::(!temp))))::(!results))
         else if (special (!new) != special (!old)) || (parens (!old)) || (parens (!new))
         then if ((!temp) != []) then
                (results::=(implode (rev (!temp)));temp:=[(!new)];q())
              else q()
         else if Char.isSpace (!new)
         then if (!temp) != [] then
                (results::=(implode (rev (!temp)));temp:=[];q())
              else q()
         else (temp::=(!new);q ()) end
  in q () end
    fun prettyPrint toStr exp = let
        fun str (Cex c) = toStr c
          | str (Vex v) = v
          | str (Bex (s,l,r)) = let
            fun rhs (Cex c) = str (Cex c)
              | rhs (Vex v) = str (Vex v)
              | rhs (Bex (s,l,r)) = ("("^(str l)^" "^s^" "^(rhs r)^")")
          in
            String.concat
                ((str l)::" "::s::" "::(rhs r)::[]) end
                (*l->str sp   op   sp   (    r->str  )*)
    in str exp end
  end
