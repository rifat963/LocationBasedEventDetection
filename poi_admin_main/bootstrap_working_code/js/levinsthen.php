<?php
$input="Istituto Di Cura Citta' Di Pavia";
$words="Istituto di cura CittÃ  di Pavia ";
$lev = levenshtein($input, $words);
echo $lev;
/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

