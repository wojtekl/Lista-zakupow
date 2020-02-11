<?php

require "repository.php";

$kraj = "en-GB";
$httpAcceptLanguage = explode(",", $_SERVER["HTTP_ACCEPT_LANGUAGE"]);
if (isset($httpAcceptLanguage[0])) {
  $kraj = $httpAcceptLanguage[0];
}

if(isset($_GET["lang"])) {
  $kraj = $_GET["lang"];
}

$identyfikator = $_GET["identyfikator"];

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
  header("Content-Type: application/json");
  global $kraj, $identyfikator, $repository;
  $result = $repository -> getCenyAll($kraj);
  $list = "[";
  foreach ($result as $row) {
    $list .= "[\"${row["PRODUKT"]}\", \"${row["SKLEP"]}\", \"${row["CENA"]}\", 0],";
  }
  $list .= "]";
  echo str_replace(",]", "]", $list);
}

function post() {
  header("Content-Type: application/json");
  global $kraj, $identyfikator, $repository;
  $result = $repository -> getCenyAll($kraj);
  $list = "[";
  foreach ($result as $row) {
    $list .= "{\"produkt\": \"${row["PRODUKT"]}\", \"sklep\": \"${row["SKLEP"]}\", \"cena\": \"${row["CENA"]}\", \"dodano\": \"${row["DODANO"]}\"},";
  }
  $list .= "]";
  echo str_replace(",]", "]", $list);
}

function put() {
  header("Content-Type: application/json");
  global $kraj, $identyfikator, $repository;
}

function delete() {
  header("Content-Type: application/json");
  global $kraj, $identyfikator, $repository;
}

?>
