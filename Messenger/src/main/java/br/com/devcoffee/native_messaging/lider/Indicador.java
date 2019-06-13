package br.com.devcoffee.native_messaging.lider;

import com.sun.jna.Library;

    public interface Indicador extends Library {

        /**
         * Metedo: openSerial Descrição: Inicia a comunicação via serial
         * passando paramentro da porta e a velocidade, returna true como
         * sucesso ou false.
         *
         * @param porta
         * @param baudeRate
         * @return true ou false
         */
        public boolean openSerial(String porta, int baudeRate);

        /**
         * Metedo: openTcp Descrição: Inicia a comunicação via rede Tcp passando
         * paramentro do host e a porta da rede, returna true como sucesso ou
         * false.
         *
         * @param host
         * @param porta
         * @return true ou false
         */
        public boolean openTcp(String host, int porta);

        /**
         * Metedo: getPeso Descrição: obtem o peso liquido da balança
         *
         * @return peso liquido da balanca
         */
        public String getPeso();

        /**
         * Metedo: getTara Descrição: obtem a tara do indicador
         *
         * @return tara do indicador
         */
        public String getTara();

        /**
         * Metedo: getPesoBruto Descrição: obtem o pesoBruto da balança
         *
         * @return pesoBruto da balança
         */
        public String getPesoBruto();

        /**
         * Metedo: getStatus Descrição: obtem o status atual do indicador
         *
         * @return status do indicador
         */
        public int getStatus();

        /**
         * Metedo: close Descrição: fecha a conexão serial ou tcp, retorna true
         * ou false
         *
         * @return conexão fecha true ou false
         */
        public boolean close();

        /**
         * Metedo: isOpen Descrição: cheque, retorna true ou false
         *
         * @return conexão fecha true ou false
         */
        public boolean isOpen();

        /**
         * Metedo: waiting Descrição: Espera a transação do buffer está pronta
         * para leitura. Obs. Essa função é apenas usada dentro de um loop
         *
         */
        public void waiting();

        /**
         * Metedo: tarar Descrição: Indicador executa a função tara da balança se estiver com peso.
         *
         * retorno: tipo dado = inteiro 
         * 
         *  0 = (TARA SEMI AUTOMATICA OK) 
         *  1 = (LIMPESA MANUAL DE TARA)
         *  2 = (SE TARA SUCESSIVA OK) 
         *  3 = (LIMPA VALOR DA TARA Tara = 0) 
         *  4 = (TARA MANUAL ON) 
         * -1 = (ERRO TARA ENVIADA OU PESO SOBRE A CELULA > CAPACIDADE) 
         * -2 = (ERRO TEM TARA ATIVA) 
         * -3 = (ERRO TARA AUTOMATICA ESTA ON) 
         * -4 = (ERRO PESO > 0 NÃO PODE TARAR) 
         * -5 = (MODO VALOR DE PICO NÃO TEM TARA) 
         * -6 = (PESO == O ENVIAR TARA COM VALOR NÃO ENVIAR SOLICITAÇÃO DE TARA!!!) 
         * -7 = (TARA SUCESSIVA ON, ENVIAR SOLICITAÇÃO DE TARA COMO PESO SOBRE CELULA) 
         * -8 = (INDICADOR NÃO ESTÁ RESPONDENDO)
         *
         *
         * Obs. Esse Essa função apenas está disponivel apartir da versão A8 do
         * indicador.
         *
         *
         * @return status 4 a -8 
         */
        public int tarar();
        
        /**
         * Metedo: tarar Descrição: Indicador executa a função tara manual da balança se estiver zerada.
         *
         * retorno: tipo dado = inteiro 
         * 
         *  0 = (TARA SEMI AUTOMATICA OK) 
         *  1 = (LIMPESA MANUAL DE TARA)
         *  2 = (SE TARA SUCESSIVA OK) 
         *  3 = (LIMPA VALOR DA TARA Tara = 0) 
         *  4 = (TARA MANUAL ON) 
         * -1 = (ERRO TARA ENVIADA OU PESO SOBRE A CELULA > CAPACIDADE) 
         * -2 = (ERRO TEM TARA ATIVA) 
         * -3 = (ERRO TARA AUTOMATICA ESTA ON) 
         * -4 = (ERRO PESO > 0 NÃO PODE TARAR) 
         * -5 = (MODO VALOR DE PICO NÃO TEM TARA) 
         * -6 = (PESO == O ENVIAR TARA COM VALOR NÃO ENVIAR SOLICITAÇÃO DE TARA!!!) 
         * -7 = (TARA SUCESSIVA ON, ENVIAR SOLICITAÇÃO DE TARA COMO PESO SOBRE CELULA) 
         * -8 = (INDICADOR NÃO ESTÁ RESPONDENDO)
         *
         *
         * Obs. Esse Essa função apenas está disponivel apartir da versão A8 do
         * indicador.
         *         
         * @param peso da tara desejada 
         * @return status 4 a -8 
         */
        public int tararManual(int peso);
        
        /**
         * Metedo: zerar 
         * Descrição: Indicador executa a função zerar.
         *
         * retorno: tipo dado = boolean
         * 
         * @return true ou false como sucesso 
         */
        public boolean zerar();

    }// fim da interface
