(ns speculator.flow
  (:require [clojure.tools.analyzer.jvm :as ana.jvm]
            [clojure.tools.analyzer.jvm.utils :as utils]
            [clojure.tools.analyzer.env :as env]
            [clojure.tools.reader.reader-types :as readers]))

(defn analyze [form & opts]
  (apply ana.jvm/analyze form opts))

(defn analyze-form
  ([form]
   (analyze-form form {}))
  ([form specs]
   (let [locals {}]
     (analyze form (assoc (ana.jvm/empty-env) :locals locals)))))
