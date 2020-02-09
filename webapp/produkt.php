<?php

$jezyk = 'en-GB';
$httpAcceptLanguage = explode(",", $_SERVER["HTTP_ACCEPT_LANGUAGE"]);
if (isset($httpAcceptLanguage[0])) {
  $jezyk = $httpAcceptLanguage[0];
}

switch ($_SERVER["REQUEST_METHOD"]) {
  case "POST":
    post();
    break;
  case "PUT":
    put();
    break;
  case "DELETE":
    delete();
    break;
  default:
    get();
}

function get() {
  global $jezyk;
  header("Content-Type: application/json");
  $row = "";
  try {
    $conn = new PDO("mysql:host=mysql1.ugu.pl;dbname=db697208", "db697208", "dupeczka");
    $conn -> setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    $stmt= $conn -> prepare("SELECT `ID`, `CENA` FROM `CENA`;");
    $stmt -> execute();
    $result = $stmt -> fetchAll();
    foreach ($result as $r) {
      $row = $r["CENA"];
    }
    $conn = null;
  }
  catch(PDOException $e) {
    echo $e -> getMessage();
  }
  echo "{\"jezyk\": \"$jezyk\",\"method\": \"$row\"}";
}

function post() {
  global $jezyk;
  header("Content-Type: application/json");
  echo "{\"jezyk\": \"$jezyk\",\"method\": \"POST\"}";
}

function put() {
  global $jezyk;
  header("Content-Type: application/json");
  echo "{\"jezyk\": \"$jezyk\",\"method\": \"PUT\"}";
}

function delete() {
  global $jezyk;
  header("Content-Type: application/json");
  echo "{\"jezyk\": \"$jezyk\",\"method\": \"DELETE\"}";
}

?>
