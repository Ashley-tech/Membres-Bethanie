<?php
    require_once('../modeles/connexion.php');
    require_once('../modeles/compte.php');

    if (isset($_GET['compte'])){
        $c = $_GET['compte'];
        $coBd = new Connexion("paris-bethanie_membre_test");
        $co = $coBd->connexion();
        $l = str_replace("'","''",$_POST['libelle']);
        $p = $_GET['personne'];

        $result = mysqli_query($co, "SELECT id_lien FROM lienparente WHERE libelle='$l'") or die("Exception");
        if (mysqli_num_rows($result) == 0){
            echo "Lien inexistant";
        } else {
            while ($line = mysqli_fetch_assoc($result)) {
                $lien = $line['id_lien'];
            }

            $result0 = mysqli_query($co, "SELECT count(*) as c FROM lienpersonne WHERE lien=$lien AND personne=$p") or die("Exception");
            while ($lien0 = mysqli_fetch_assoc($result0)){
                $count = $lien0['c'];
            }
            if ($count == 0){
                echo "Non associé au lien";
            } else {
                mysqli_query($co, "DELETE from lienpersonne WHERE lien=$lien AND personne = $p") or die("Exception");
                mysqli_query($co, "INSERT INTO historiquepersonne (texte, date_historique, heure_historique, supprime, personne_concerne, compte_concerne) VALUES ('Vous avez attribué de nouveaux liens de parenté à cette personne',curdate(),curtime(),false,$p,$c)");

                $s = "";

                    $result1 = mysqli_query($co,"SELECT libelle,mel,sexe,nom,prenom FROM lienpersonne lpe, lienparente lpa, personne WHERE lien=id_lien AND personne=id_personne AND personne=$p");
                    while ($e = mysqli_fetch_assoc($result1)){
                        $s = $s.'<li>'.$e['libelle'].'</li>';
                        $mel = $e['mel'];
                        $prenom = $e['prenom'];
                        $nom = strtoupper($e['nom']);
                        if ($e['sexe'] == 'M'){
                            $sexe = 'M.';
                        } else {
                            $sexe = "Mme";
                        }
                    }$headers[] = 'MIME-Version: 1.0';
     $headers[] = 'Content-type: text/html; charset=iso-8859-1';
                    $headers[] = 'From: noreply@paris-bethanie.fr <noreply@paris-bethanie.fr>';
     $headers[] = 'Cc: noreply@paris-bethanie.fr <noreply@paris-bethanie.fr>';
	mail($mel,"Modifications","Bonjour $sexe $nom $prenom, <br /><br />L'église protestante de Béthanie vous a retiré un lien de parenté : <b>".$_POST['libelle']."</b><br /><br />Voici votre nouvelle liste des liens de parentés :<br />$s<br /><br />Cordialement,<br /><br />Le conseil présbytéral<br /><img src='../images/logo.png' />",implode("\r\n", $headers));

                echo "Succès";
            }
        }
    } else {
        echo "Echec";
    }    
?>