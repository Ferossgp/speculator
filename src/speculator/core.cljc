(ns speculator.core
  (:require  [speculator.utils :as su]
             [clojure.spec.alpha :as s]
             [clojure.tools.namespace.file :as ns-file]
             [clojure.tools.namespace.parse :as ns-parse]
             [clojure.java.io :as io]
             [speculator.flow :as flow]
             [speculator.types :as types]
             [clojure.tools.namespace.track :refer [tracker]]))

(declare emit)
(defn concat-emits [& xs]
  (apply str xs))

(defn emits
  ([])
  ([^Object a]
   (cond
     (nil? a) nil
     (map? a) (emit a)
     (seq? a) (apply emits a)
     (fn? a)  (a)
     :else    (let [^String s (cond-> a (not (string? a)) .toString)]
                s)))
  ([a & xs]
   (concat-emits
    (emits a)
    (apply concat-emits (map emits xs)))))

(defn _emitln []
  "\n")

(defn emitln [& xs]
  (concat-emits
   (apply emits xs)
   (_emitln)))

(defn emit-var [node]
  (:form node))

(defmacro emit-wrap [env & body]
  `(let [env# ~env]
     (when (= :return (:context env#)) (emits "r := "))
     ~@body
     (when-not (= :expr (:context env#)) (emitln ";"))))

(defn- comma-sep [xs]
  (interpose "," xs))

(defmulti speculate :op)

(defn emit [ast]
  (speculate ast))

(defn emit-let [ast is-loop])

(defmethod speculate :const [c]
  (let [{:keys [form]} c]
    (types/emit-literal* form)))

(defmethod speculate :def [c]
  (let [{:keys [name var init env var-ast]} c]
    (if-not (= :with-meta (get init :op))
      (concat-emits
       (emitln "const " name ": " (types/emit-literal-type* (get-in init [:form])) ";")
       (emitln "axiom " name " == " (get-in init [:form]) ";"))
      (emit (merge {:name name} init)))))

(defmethod speculate :with-meta [c]
  (emits (merge {:name (:name c)}
                (:expr c))))

(defn procedure-call [c]
  (let [name      (or (:speculate-name c)
                      (su/unique-var))
        args      (seq (:args c))
        args-call (reduce (fn [acc arg]
                            (if (contains? #{:fn :static-call :invoke} (:op arg))
                              (let [n (su/unique-var)]
                                (-> acc
                                    (update :binding concat (speculate (assoc arg
                                                                              :speculate-name n
                                                                              :raw? true)))
                                    (update :args conj n)))
                              (update acc :args conj (speculate arg))))
                          {:args    []
                           :binding []}
                          args)]
    (if (:raw? c)
      (conj (:binding args-call)
            {:var-name (emitln "var " name ":" (types/emit-tag-type* (:class c)) ";")
             :var-body (emitln "call " name " := " (:method c) "(" (comma-sep (:args args-call)) ");")})
      (concat-emits
       (->> (:binding args-call)
            (map :var-name)
            (apply concat-emits))
       (emitln "var " name ":" (types/emit-tag-type* (:class c)) ";")
       (->> (:binding args-call)
            reverse
            (map :var-body)
            (apply concat-emits))
       (emitln "call " name " := " (:method c) "(" (comma-sep (:args args-call)) ");")))))

(defmethod speculate :static-call [c] (procedure-call c))

(defmethod speculate :invoke [c] (procedure-call c))

(defn emit-fn-method [{expr :body :keys [type name params env recurs]}]
  (concat-emits
   (when recurs
      (emitln "while(true){"))
   (emits expr)
   (when recurs
     (concat-emits
      (emitln "break;")
      (emitln "}")))))

(defmethod speculate :fn [{:keys [name speculate-name methods raw?] :as node}]
  (println (count methods))
  (let [name      (or speculate-name name (su/unique-var))
        params    (->> (mapcat :params methods)
                       (map #(str (:form %) ":"
                                  (types/emit-tag-type* (:tag %)))))
        maxparams (apply max-key count (map :params methods))
        mmap      (into {}
                        (map (fn [method]
                               [(symbol (str name "__" (count (:params method))))
                                method])
                             methods))
        ms        (sort-by #(-> % second :params count) (seq mmap))
        ret       (first (last ms))]
    (concat-emits
      (emitln "procedure " name "(" (comma-sep params) ")"
              " returns (r:"  (types/emit-tag-type* (:return-tag node)) ")")
      (emitln "{")
      (->> ms
           (map second)
           (map emit-fn-method)
           (apply concat-emits))
      (emitln "r := " ret ";")
      (emitln "}"))))

(defmethod speculate :fn-method [node] (print "fn-method"))

(defmethod speculate :do [{:keys [statements ret env] :as node}]
  (concat-emits
   (apply concat-emits (map emitln statements))
   (emit ret)))

(defmethod speculate :var [node] (emit-var node))
(defmethod speculate :binding [node] (emit-var node))
(defmethod speculate :local [node] (emit-var node))

(defmethod speculate :new [node] (print "new"))
(defmethod speculate :let [node] (emit-let node false))
(defmethod speculate :letfn [node] (print "letfn"))
(defmethod speculate :vector [node] (print "vector"))
(defmethod speculate :set [node] (print "set"))
(defmethod speculate :catch [node] (print "catch"))
(defmethod speculate :if [node] (print "if"))
(defmethod speculate :invoke [node] (print "invoke"))
(defmethod speculate :loop [node] (emit-let node true))
(defmethod speculate :map [node] (print "map"))
(defmethod speculate :recur [node] (print "recur"))
(defmethod speculate :throw [note] (print "throw"))
(defmethod speculate :try [node] (print "try"))

(defmethod speculate :default [c]
  (print (:op c)))


(defn -main [& args]
  (for [arg args]
    (let [code       (load-file arg)
          file-name  (str arg ".bpl")
          ast        (flow/analyze-form code)
          translated (emit ast)]
      (println "Translating: " code)
      (println "Ast:" ast)
      (spit file-name translated))))
