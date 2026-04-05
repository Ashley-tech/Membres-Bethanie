<?php
    $co =mysqli_connect("mysql-paris-bethanie.alwaysdata.net","299683","Gambetta","paris-bethanie_membre_test") or die("Erreur de connexion !");
    $response = array();
    if (!empty($_GET['lien'])){
        if ($co){
            $l = $_GET['lien'];
            $sth = mysqli_query($co, "SELECT definition, sexe_particulier FROM lienparente WHERE libelle = '".str_replace("'","''",$l)."'");
            if (mysqli_num_rows($sth) > 0){
                $rows = array();
                $response["lien"] = array();
                while($r = mysqli_fetch_array($sth)) {
                    $rows['definition'] = $r['definition'];
                    if ($r['sexe_particulier'] == 'F'){
                        $rows['sexe_particulier'] = 'Femme';
                    } else if ($r['sexe_particulier'] == 'M'){
                        $rows['sexe_particulier'] = 'Homme';
                    } else {
                        $rows['sexe_particulier'] = 'Non';
                    }

                    array_push($response["lien"],$rows);
                }
                $response["success"]=1;
            } else {
                $response["success"]=0;
                $response["message"]="Aucune information !";
            }
        } else {
        $response["success"]=0;
                $response["message"]="Aucune connexion !";
        }
    } else {
        $response["success"]=0;
                $response["message"]="Aucun lien séléctionné !";
    }
    echo json_encode($response);
?>