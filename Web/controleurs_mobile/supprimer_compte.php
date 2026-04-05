<?php
	require_once('../modeles/compte.php');
    require_once('../modeles/connexion.php');
	$c = $_GET['compte'];
		$coBd = new Connexion("paris-bethanie_membre_test");
		$co = $coBd->connexion();

		$result = mysqli_query($co, "UPDATE compte SET statut_compte='Supprimé', date_suppression=curdate(), heure_suppression=curtime() WHERE id_compte=$c");
?>