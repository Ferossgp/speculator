(ns speculator.utils)

(def vc* (atom {:id     0
                :offset 10
                :msb    35
                :power  1}))

(defn unique-var []
  (let [{:keys [id offset msb power]} @vc*
        vc (+ id offset)]
    (swap! vc* #(cond-> (assoc % :id (inc id))
                  (= vc msb) (assoc :offset (+ offset (* 9 (inc msb))))
                  (= vc msb) (assoc :msb (dec (Math/pow 36 (inc power))))
                  (= vc msb) (assoc :power (inc power))))
    (str "var" (Integer/toString vc 36))))
