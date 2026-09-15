package academy.digitallab.onlinestore.shopping.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_invoices")
public class InvoiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number_invoice")
    private String numberInvoice;

    private String description;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "create_at")
    private LocalDate createAt;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "invoice_id")
    private List<InvoiceItemEntity> items = new ArrayList<>();

    private String state;

    public InvoiceEntity() {
    }

    @PrePersist
    void onCreate() {
        if (createAt == null) {
            createAt = LocalDate.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumberInvoice() {
        return numberInvoice;
    }

    public void setNumberInvoice(String numberInvoice) {
        this.numberInvoice = numberInvoice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public LocalDate getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDate createAt) {
        this.createAt = createAt;
    }

    public List<InvoiceItemEntity> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItemEntity> items) {
        this.items = items;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
