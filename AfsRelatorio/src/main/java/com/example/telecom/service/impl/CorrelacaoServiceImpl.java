package com.example.telecom.service.impl;

import com.example.telecom.domain.model.IpInterface;
import com.example.telecom.domain.model.Rota;
import com.example.telecom.repository.IpInterfaceRepository;
import com.example.telecom.repository.RotaRepository;
import com.example.telecom.service.CorrelacaoService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CorrelacaoServiceImpl implements CorrelacaoService {

    private final IpInterfaceRepository ipRepo;
    private final RotaRepository rotaRepo;

    // injeção via construtor
    public CorrelacaoServiceImpl(IpInterfaceRepository ipRepo, RotaRepository rotaRepo) {
        this.ipRepo = ipRepo;
        this.rotaRepo = rotaRepo;
    }

    @Override
    public List<String> validarRotasVsInterfaces(Long equipamentoId) {
        List<IpInterface> ips = ipRepo.findByInterfaceRede_Equipamento_Id(equipamentoId);
        List<Rota> rotas = rotaRepo.findByEquipamento_Id(equipamentoId);
        List<String> avisos = new ArrayList<>();

        for (IpInterface ip : ips) {
            int ipInt = toInt(ip.getIp());
            int mask = maskFromPrefix(ip.getPrefixo());
            int rede = ipInt & mask;

            boolean temRota = rotas.stream().anyMatch(r -> {
                int rDest = toInt(r.getDestino());
                int rMask = maskFromPrefix(r.getPrefixo());
                return rDest == rede && rMask == mask;
            });

            if (!temRota) {
                var iface = ip.getInterfaceRede();
                avisos.add(String.format(
                    "⚠️ Interface %s (%s) está com IP %s/%d, mas nenhuma rota está configurada para a rede %s/%d.",
                    iface.getPorta(), iface.getNome(), ip.getIp(), ip.getPrefixo(), intToIp(rede), ip.getPrefixo()
                ));
            }
        }

        for (Rota r : rotas) {
            int gw = toInt(r.getGateway());
            boolean gwAlcancavel = ips.stream().anyMatch(ip -> {
                int mask = maskFromPrefix(ip.getPrefixo());
                int redeIp = toInt(ip.getIp()) & mask;
                return (gw & mask) == redeIp;
            });
            if (!gwAlcancavel) {
                avisos.add(String.format(
                    "⚠️ Rota %s/%d possui gateway %s não alcançável por nenhum IP de interface.",
                    r.getDestino(), r.getPrefixo(), r.getGateway()
                ));
            }
        }

        return avisos;
    }

    private static int toInt(String ip){
        String[] p = ip.split("\\.");
        return (Integer.parseInt(p[0])<<24)|(Integer.parseInt(p[1])<<16)|(Integer.parseInt(p[2])<<8)|Integer.parseInt(p[3]);
    }
    private static String intToIp(int v){
        return ((v>>>24)&0xFF)+"."+((v>>>16)&0xFF)+"."+((v>>>8)&0xFF)+"."+(v&0xFF);
    }
    private static int maskFromPrefix(int pr){ return pr==0?0:-1 << (32-pr); }
}
