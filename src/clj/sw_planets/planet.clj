(ns sw-planets.planet
  (:require [datomic.client.api :as d]
            [sw-planets.db :as db]
            [clj-http.client :as http-client]
            [clojure.data.json :as json]))

(defn uuid [] (java.util.UUID/randomUUID))
(defn uuidFromString [uuid] (java.util.UUID/fromString uuid))

(def cfg {:server-type :peer-server
          :access-key "myaccesskey"
          :secret "mysecret"
          :endpoint "localhost:8998"})

;; (def client (d/client cfg))
;; (def conn (d/connect client {:db-name "hello"}))

(defn search-planet-apparitions
  "Search planet apparitions in Star Wars movies using https://swapi.co/"
  [name]
  (let [response (http-client/get (str "https://swapi.co/api/planets/?search=" name))
        planet-results (get (json/read-str (get response :body) :key-fn keyword) :results)]
    (when (> (count planet-results) 0)
      (get (first planet-results) :films))))

(defn find-all-planets
  "Returns a list of all planets in the DB."
  []
  (let [query '[:find ?id ?name (distinct ?climate) (distinct ?terrain) (distinct ?movies)
                :where
                [?e :planet/id ?id]
                [?e :planet/name ?name]
                [?e :planet/climate ?climate]
                [?e :planet/terrain ?terrain]
                [?e :planet/apparitions ?movies]]
        db (d/db (db/get-conn))]
    (d/q query db)))

(defn find-planet-by-name
  "Returns a single object from the DB."
  [name]
  (let [query '[:find ?id (distinct ?climate) (distinct ?terrain) (distinct ?movies)
                :in $ ?name
                :where
                [?e :planet/id ?id]
                [?e :planet/name ?name]
                [?e :planet/climate ?climate]
                [?e :planet/terrain ?terrain]
                [?e :planet/apparitions ?movies]]
        db (d/db (db/get-conn))]
    (first (d/q query db (clojure.string/capitalize name)))))

(defn find-planet-by-id
  "Returns a single object from the DB."
  [id]
  (let [query '[:find ?name (distinct ?climate) (distinct ?terrain) (distinct ?movies)
                :in $ ?id
                :where
                [?e :planet/id ?id]
                [?e :planet/name ?name]
                [?e :planet/climate ?climate]
                [?e :planet/terrain ?terrain]
                [?e :planet/apparitions ?movies]]
        db (d/db (db/get-conn))]
    (first (d/q query db (uuidFromString id)))))

(defn remove-planet
  [uuid-string]
  (let [uuid (uuidFromString uuid-string)
        tx [[:db/retractEntity [:planet/id uuid]]
            [:db/add "datomic.tx" :db/doc "Removing planet."]]]
    (d/transact (db/get-conn) {:tx-data tx})))

(defn add-planet
  "Inserts a planet to the DB."
  [name climate terrain]
  (if (not (find-planet-by-name name))
    (let [capitalized-name (clojure.string/capitalize name)
          movie-apparitions (search-planet-apparitions name)
          planet [{:planet/id (uuid)
                   :planet/name capitalized-name
                   :planet/climate climate
                   :planet/terrain terrain
                   :planet/apparitions (or movie-apparitions "0")
                   }]]
      (println planet)
      (d/transact (db/get-conn) {:tx-data planet}))
    (println "Planet já cadastrado")))


;; (require '[sw-planets.planet :as planet] '[sw-planets.db :as db])
