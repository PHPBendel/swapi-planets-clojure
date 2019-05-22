(ns sw-planets.routes.home
  (:require
   [sw-planets.middleware :as middleware]
   [sw-planets.planet :as planet]
   ))

(defn get-pedro [request]
  {:body '[{:name "Pedro"
            :terrain "Montanhoso"
            :climate "Chuvoso"}
           {:name "Bendel"}]})

(defn get-all-planets
  []
  {:body
   (map (fn [a]
          (let [planet a]
            {:uuid (nth planet 0)
             :name (nth planet 1)
             :climate (nth planet 2)
             :terrain (nth planet 3)
             :movies (nth planet 4)}))
        (planet/find-all-planets))})

(defn get-planet-by-name
  [name]
  (let [planet (planet/find-planet-by-name name)]
    {:body {:uuid (nth planet 0)
            :name name
            :climate (nth planet 1)
            :terrain (nth planet 2)
            :movies (nth planet 3)}}))

(defn get-planet-by-uuid
  [uuid]
  (let [planet (planet/find-planet-by-id uuid)]
    {:body {:uuid uuid
            :name (nth planet 0)
            :climate (nth planet 1)
            :terrain (nth planet 2)
            :movies (nth planet 3)}}))

(defn handle-request
  [request]
  (let [params (get request :params)]
    (cond
      (and (get params :uuid) (not (get params :name))) (get-planet-by-uuid (get params :uuid))
      (get params :name) (get-planet-by-name (get params :name))
      :else (get-all-planets))))

(defn handle-post
  [request]
  (let [params (get request :params)
        name (get params :name)
        climate (get params  :climate)
        terrain (get params :terrain)]
    (println params)
    (when (and name climate terrain)
      (planet/add-planet name climate terrain)
      {:body "Planet successfully added!"})))

(defn handle-delete
  [request]
  (let [params (get request :params)
        uuid (get params :uuid)]
    (planet/remove-planet uuid)
    {:body "Planet successfully removed!"}))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-formats]}
   ["/" {:get get-pedro}]
   ["/planets" {:get handle-request
                :post handle-post
                :delete handle-delete}]])
