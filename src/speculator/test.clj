(ns speculator.test)


(defn fn1 [x]
  (= 1 x))

(defn fn2 []
  (fn1 1))
