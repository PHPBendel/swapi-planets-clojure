(ns user
  "Userspace functions you can run by default in your local REPL."
  (:require
    [sw-planets.config :refer [env]]
    [clojure.spec.alpha :as s]
    [expound.alpha :as expound]
    [mount.core :as mount]
    [sw-planets.core :refer [start-app]]))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(defn start 
  "Starts application.
  You'll usually want to run this on startup."
  []
  (mount/start-without #'sw-planets.core/repl-server))

(defn stop 
  "Stops application."
  []
  (mount/stop-except #'sw-planets.core/repl-server))

(defn restart 
  "Restarts application."
  []
  (stop)
  (start))


