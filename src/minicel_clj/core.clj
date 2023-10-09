(ns core
  (:require [coffi.ffi :as cfn]
            [clojure.tools.cli :as cli]
            [clojure.string :as str]
            [clojure.data.csv :as csv]
            ))

(defn- safe-slurp
  "exception handling for slurping file."
  [file]
  (try
    (slurp file)
    (catch java.io.FileNotFoundException e
      (println "File not found: " (.getMessage e)))
    (finally
      (println "Attempted to read app data file"))))

(def cli-options
  [["-f" "--file NAME" "target file name"]
   ["-h" "--help" "Show this"]])

(defn usage [options-summary]
  (->> ["This is my program. There are many like it, but this one is mine."
        ""
        "Usage: program-name -f file-name"
        ""
        "Options:"
        options-summary
        "Please refer to the manual page for more information."]
       (str/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (str/join \newline errors)))

(defn validate-args
  [args]
  (let [{:keys [options arguments errors summary]} (cli/parse-opts args cli-options)]
    (cond

      (:help options)
      {:exit-message (usage summary) :ok? true}

      (:file options)
      {:file-name (:file options)})))

(validate-args ["-h" ])
;; => {:exit-message "This is my program. There are many like it, but this one is mine.\n\nUsage: program-name -f file-name\n\nOptions:\n  -f, --file NAME  target file name\n  -h, --help       Show this\nPlease refer to the manual page for more information.", :ok? true}
(validate-args ["-f" "resources/test.csv"])
;; => {:file-name "resources/test.csv"}

(defn exit [status msg]
  (println msg)
  (System/exit status)
  )

(defn run [& args]
  (let [{:keys [file-name exit-message ok?]} (validate-args args)]
    (if exit-message
      (exit (if ok? 0 1) exit-message)
      (mapv #(str/split % #"\|") (str/split-lines (safe-slurp file-name))))))



(safe-slurp "hello.txt")
(run "--file" "resources/test.csv")
;; => [["A" "B"] ["1" "2"] ["3" "4"] ["=A1+B1" " =A2+B2"] ["if(A, B)" " R"] [""] ["A" "B"] ["1" "2"] ["3" "4"] ["3" "7"]]
;; => ["A|B" "1|2" "3|4" "=A1+B1| =A2+B2" "if(A, B)| R" "" "A|B" "1|2" "3|4" "3|7"]
;; => "A|B\n1|2\n3|4\n=A1+B1| =A2+B2\nif(A, B)| R\n\nA|B\n1|2\n3|4\n3|7\n"


