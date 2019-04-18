(ns speculator.types
  (:require [clojure.tools.analyzer.jvm.utils :as jvm.utils])
  (:import (clojure.lang RT Symbol Var)
           org.objectweb.asm.Type))

(defmulti emit-literal* class)

(defmethod emit-literal* Long [x] x)

(defmethod emit-literal* String [x] (str "\" " x "\""))

(defmethod emit-literal* :default [x] nil)


;; AnyInteger => Integer Long clojure.lang.BigInt BigInteger Short Byte
;; Integer => Integer Long clojure.lang.BigInt BigInteger Short Byte
;; Number => Number
;; Double => java.lang.Double
;; Keyword => clojure.lang.Keyword
;; Symbol => clojure.lang.Symbol
;; String => java.lang.String
;; Boolean => java.lang.Boolean
;; Namespace => clojure.lang.Namespace
;; Atom => clojure.lang.Atom
;; Var => clojure.lang.Var
;; Ref => clojure.lang.Ref
;; Agent => clojure.lang.Agent
;; Coll => clojure.lang.IPersistentCollection
;; Vector => clojure.lang.IPersistentVector
;; LazySeq => clojure.lang.LazySeq
;; Map => clojure.lang.IPersistentMap
;; Set => clojure.lang.IPersistentSet
;; Seqable => clojure.lang.Seqable
;; Sequence => clojure.lang.ISeq
;; Future =>
;; Promise =>
;; Delay => clojure.lang.Delay
;; Deref => clojure.lang.IDeref
;; BlockingDeref => clojure.lang.IBlockingDeref
;; List => clojure.lang.IPersistentList
;; Exception => clojure.lang.IExceptionInfo
;; Proxy => clojure.lang.IProxy
;; Stack => clojure.lang.IPersistentStack
;; Reversible => clojure.lang.Reversible
;; Sequential => clojure.lang.Sequential
;; Fn => clojure.lang.Fn
;; MultiFn => clojure.lang.MultiFn

(def ^:const integer "Integer")
(def ^:const number "Number")
(def ^:const kw "Keyword")
(def ^:const sym "Symbol")
(def ^:const string "String")
(def ^:const bool "Boolean")
(def ^:const object "Object")

(defmulti emit-literal-type* class)

(defmethod emit-literal-type* Long [_] integer)
(defmethod emit-literal-type* Integer [_] integer)
(defmethod emit-literal-type* clojure.lang.BigInt [_] integer)
(defmethod emit-literal-type* BigInteger [_] integer)
(defmethod emit-literal-type* Short [_] integer)
(defmethod emit-literal-type* Byte [_] integer)

(defmethod emit-literal-type* Number [_] number)

(defmethod emit-literal-type* clojure.lang.Keyword [_] kw)

(defmethod emit-literal-type* clojure.lang.Symbol [_] sym)

(defmethod emit-literal-type* String [_] string)

(defmethod emit-literal-type* Boolean [_] bool)

(defmethod emit-literal-type* :default [x]
  (println "IMPLEMENT ME:" (class x)))

(defmulti emit-tag-type* identity)

(case nil
  "byte" Byte/TYPE
  "boolean" Boolean/TYPE
  "char" Character/TYPE
  "int" Integer/TYPE
  "long" Long/TYPE
  "float" Float/TYPE
  "double" Double/TYPE
  "short" Short/TYPE
  "void" Void/TYPE
  "object" Object
  nil)

(defmethod emit-tag-type* clojure.lang.Symbol [_] sym)

(defmethod emit-tag-type* Long/TYPE [_] integer)

(defmethod emit-tag-type* clojure.lang.Numbers [_] number)

(defmethod emit-tag-type* java.lang.Object [_] object)

(defmethod emit-tag-type* String [s] string)

(defmethod emit-tag-type* :default [x] nil)
