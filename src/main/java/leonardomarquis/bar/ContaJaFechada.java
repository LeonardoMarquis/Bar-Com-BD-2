package leonardomarquis.bar;

import java.time.LocalDateTime;

public class ContaJaFechada {
    private String numeroConta;
    private String nomeCliente;
    private double valorTotal;
    private double valorPago;
    private double valorPendente;
    private LocalDateTime dataFechamento;

    public ContaJaFechada(String numeroConta, String nomeCliente, double valorTotal, double valorPago, double valorPendente, LocalDateTime dataFechamento) {
        this.numeroConta = numeroConta;
        this.nomeCliente = nomeCliente;
        this.valorTotal = valorTotal;
        this.valorPago = valorPago;
        this.valorPendente = valorPendente;
        this.dataFechamento = dataFechamento;
    }

    // Getters para exibição
    public String getNumeroConta() { return numeroConta; }
    public String getNomeCliente() { return nomeCliente; }
    public double getValorTotal() { return valorTotal; }
    public double getValorPago() { return valorPago; }
    public double getValorPendente() { return valorPendente; }

    @Override
    public String toString() {
        // Formata a saída para o terminal
        String status = (valorPendente > 0) ? "ABERTA (PARCIAL)" : "PAGA";

        return String.format(
                "| Conta: %-5s | Cliente: %-20s | Total: R$ %6.2f | Pago: R$ %6.2f | Falta Pagar: R$ %6.2f (%s)",
                numeroConta,
                nomeCliente,
                valorTotal,
                valorPago,
                valorPendente,
                status
        );
    }
}