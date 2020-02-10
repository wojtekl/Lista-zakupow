<?php

require "dane.php";

class Repository {
  private $sql;
  
  public function __construct($SQL_DATABASE, $SQL_USER, $SQL_PASSWORD) {
    $this -> sql = new PDO("mysql:host=mysql.cba.pl;dbname=$SQL_DATABASE", $SQL_USER, $SQL_PASSWORD);
    
    $this -> sql -> setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
  }
  
  public function __destruct() {
    $this -> sql = null;
  }
  
  private function execute($dml) {
    try {
      $query = $this -> sql -> prepare($dml);
      $query -> execute();
      return $query -> fetchAll();
    }
    catch(PDOException $exception) {
      return $exception -> getMessage();
    }
  }
  
  public function insertCena($nazwa, $sklep, $cena, $kraj, $identyfikator) {
    return $this -> execute("INSERT INTO `CENA` VALUES (0, '$nazwa', '$sklep', '$cena', '$kraj', '$identyfikator', CURRENT_TIMESTAMP);");
  }
  
  public function getCeny($nazwa) {
    return $this -> execute("SELECT `SKLEP`, `CENA`, `DODANO` FROM `CENA` WHERE `PRODUKT` = '$nazwa' ORDER BY `DODANO` DESC, `SKLEP`;");
  }
  
  public function getCenyAll() {
    return $this -> execute("SELECT `c`.`PRODUKT`, `c`.`SKLEP`, `c`.`CENA`, `c`.`DODANO` FROM `CENA` `c` JOIN (SELECT MAX(`ID`) AS `ID`, `PRODUKT` FROM `CENA` GROUP BY `PRODUKT`) `s` ON `s`.`ID` = `c`.`ID` ORDER BY `PRODUKT`;");
  }
}

$repository = new Repository($SQL_DATABASE, $SQL_USER, $SQL_PASSWORD);

?>
