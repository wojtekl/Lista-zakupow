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
  
  public function insertCena($produkt, $sklep, $cena, $kraj, $identyfikator) {
    return $this -> execute("INSERT INTO `CENA` VALUES (0, '$produkt', '$sklep', '$cena', '$kraj', '$identyfikator', CURRENT_TIMESTAMP);");
  }
  
  public function getCeny($produkt) {
    return $this -> execute("SELECT `SKLEP`, `CENA`, `DODANO` FROM `CENA` WHERE `PRODUKT` = '$produkt' ORDER BY `CENA`, `DODANO` DESC, `SKLEP` LIMIT 10;");
  }
  
  public function getCenyAll($kraj) {
    return $this -> execute("SELECT `s`.`PRODUKT`, `s`.`SKLEP`, `s`.`CENA`, `s`.`DODANO` FROM `CENA` `s` JOIN (SELECT `d`.`PRODUKT`, `d`.`SKLEP`, `d`.`CENA`, MAX(`d`.`DODANO`) AS `DODANO` FROM `CENA` `d` JOIN (SELECT `PRODUKT`, `SKLEP`, MIN(`CENA`) AS `CENA` FROM `CENA` WHERE `KRAJ` = '$kraj' AND `DODANO` > CURRENT_TIMESTAMP - INTERVAL 2 DAY GROUP BY `PRODUKT`) `c` ON `c`.`PRODUKT` = `d`.`PRODUKT` AND `c`.`CENA` = `d`.`CENA` GROUP BY `PRODUKT`, `CENA`) `cd` ON `cd`.`PRODUKT` = `s`.`PRODUKT` AND `cd`.`CENA` = `s`.`CENA` AND `cd`.`DODANO` = `s`.`DODANO` UNION SELECT `PRODUKT`, `SKLEP`, `CENA`, MAX(`DODANO`) AS `DODANO` FROM `CENA` WHERE `KRAJ` = '$kraj' GROUP BY `PRODUKT` HAVING `DODANO` < CURRENT_TIMESTAMP - INTERVAL 2 DAY ORDER BY `PRODUKT`;");
  }
}

$repository = new Repository($SQL_DATABASE, $SQL_USER, $SQL_PASSWORD);

?>
