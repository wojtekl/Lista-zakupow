<?php
header("Content-Type: application/json");

try {
  $conn = new PDO("mysql:host=mysql.cba.pl;dbname=mojdzien", "mojdzien", "Maslo15");
  $conn -> setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
  $stmt= $conn -> prepare(file_get_contents("ddl.sql"));
  $stmt -> execute();
  $conn = null;
}
catch(PDOException $e) {
  echo $e -> getMessage();
}
?>
