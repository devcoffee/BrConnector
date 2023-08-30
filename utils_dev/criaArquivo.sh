#!/bin/bash

# Especifique o arquivo de saída
OUTFILE="/Users/muriloht/peso.txt"

# Loop infinito
while :
do
  # Gere um número aleatório
  RANDOM_NUMBER=$RANDOM
  
  # Escreva o número aleatório no arquivo de saída
  echo $RANDOM_NUMBER > $OUTFILE
  
  # Aguarde por um segundo
  sleep 1
done
