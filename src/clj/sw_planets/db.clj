(ns sw-planets.db
  (:require [clojure.java.io :as io]
            [datomic.client.api :as d]))

(def planets-schema (io/resource "planets-schema.edn"))

(def cfg {:server-type :peer-server
          :access-key "myaccesskey"
          :secret "mysecret"
          :endpoint "localhost:8998"})

(defn- read-one
  [r]
  (try
    (read r)
    (catch java.lang.RuntimeException e
      (if (= "EOF while reading" (.getMessage e))
        ::EOF
        (throw e)))))

(defn read-all
  "Reads a sequence of top-level objects in file"
  ;; Modified from Clojure Cookbook, L Vanderhart & R. Neufeld
  [src]
  (with-open [r (java.io.PushbackReader. (clojure.java.io/reader src))]
    (binding [*read-eval* false]
      (doall (take-while #(not= ::EOF %) (repeatedly #(read-one r)))))))

(defn transact-all
  "Load and run all transactions from f, where f is any
   resource that can be opened by io/reader."
  [conn f]
  (loop [n 0
         [tx & more] (read-all f)]
    (if tx
      (recur (+ n (count (:tx-data (d/transact conn {:tx-data tx}))))
             more)
      {:datoms n})))

(defn ensure-schema
  [conn]
  (transact-all conn planets-schema))

(defn get-conn
  []
  (let [client (d/client cfg)
        conn (d/connect client {:db-name "hello"})]
    conn))
