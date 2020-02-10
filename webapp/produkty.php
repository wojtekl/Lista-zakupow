<?php

require "repository.php";

$jezyk = "en-GB";
$httpAcceptLanguage = explode(",", $_SERVER["HTTP_ACCEPT_LANGUAGE"]);
if (isset($httpAcceptLanguage[0])) {
  $jezyk = $httpAcceptLanguage[0];
}

$identyfikator = $_POST["identyfikator"];

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
  global $jezyk, $identyfikator, $repository;
  $result = $repository -> getCenyAll();
  $lista = "[";
  foreach ($result as $r) {
    $lista .= "[\"${r["PRODUKT"]}\", \"${r["SKLEP"]}\", \"${r["CENA"]}\", 0],";
  }
  $lista[strlen($lista) - 1] = "]";
  echo $lista;
}

function post() {
  header("Content-Type: application/json");
  global $jezyk, $identyfikator, $repository;
  $result = $repository -> getCenyAll();
  $lista = "[";
  foreach ($result as $r) {
    $lista .= "{\"produkt\": \"${r["PRODUKT"]}\", \"sklep\": \"${r["SKLEP"]}\", \"cena\": \"${r["CENA"]}\", \"dodano\": \"${r["DODANO"]}\"},";
  }
  $lista[strlen($lista) - 1] = "]";
  echo $lista;
}

function put() {
  header("Content-Type: application/json");
  global $jezyk, $identyfikator, $repository;
}

function delete() {
  header("Content-Type: application/json");
  global $jezyk, $identyfikator, $repository;
}

?>
