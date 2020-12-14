package com.mitocode.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.mitocode.model.Factura;
import com.mitocode.repo.IClienteRepo;
import com.mitocode.repo.IFacturaRepo;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.repo.IPlatoRepo;
import com.mitocode.dto.FiltroDTO;
import com.mitocode.service.IFacturaService;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service //estereotipo usado en el caso del repositorio (DAO) no se agrega @Repository ya que hereda de una clase que ya lo es
public class FacturaServiceImpl  extends CRUDImpl<Factura, String> implements IFacturaService{

	@Autowired
	private IFacturaRepo repo;
	
	@Autowired
	private IClienteRepo clienteRepo;

	@Autowired
	private IPlatoRepo platoRepo;
	@Override
	protected IGenericRepo<Factura, String> getRepo() {

		return repo;
	}

	@Override
	public Flux<Factura> obtenerFacturasPorFiltro(FiltroDTO filtro) {
		String criterio =  filtro.getIdCliente() != null ? "C" : "O";
		if (criterio.equals("C")) {
			return repo.obtenerFacturaPorCliente(filtro.getIdCliente());
		} else {
			return repo.obtenerFacturasPorFecha(filtro.getFechaInicio(), filtro.getFechaFin());
		}
	}

	@Override
	public Mono<byte[]> generarReporte(String idFactura) {
		// TODO Auto-generated method stub
		return repo.findById(idFactura)
				//obteniendo cliente
				.flatMap(f -> {
					return Mono.just(f)
							.zipWith(clienteRepo.findById(f.getCliente().getId()), (fa,cl) ->{
								fa.setCliente(cl);
								return fa;
							});
				})
				//obteniendo cada plato
				.flatMap(f -> {
					return Flux.fromIterable(f.getItems()).flatMap(it -> {
						return platoRepo.findById(it.getPlato().getId())
								.map(p -> {
									it.setPlato(p);
									return it;
								});
					}).collectList().flatMap(list -> {
						//setteando la nueva lista a la factura
						f.setItems(list);
						return Mono.just(f); //devolviendo la factura para el siguiente operador
					});
				})
				// obteniendo bytes[]
				.map(f -> {
					File file;
					try {
						Map<String, Object> parametros = new HashMap<String,Object>();
//						parametros.put("txt_cliente", f.getCliente().getNombres() + " " + f.getCliente().getApellidos());
						
						file = new ClassPathResource("/reports/factura.jrxml").getFile();
						JasperCompileManager.compileReportToFile(file.getPath());
						File jasper = new ClassPathResource("/reports/factura.jrxml").getFile();
						JasperPrint print = JasperFillManager.fillReport(jasper.getPath(), parametros, new JRBeanCollectionDataSource(f.getItems()));
						return JasperExportManager.exportReportToPdf(print);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return new byte[0];
				});
	}
	
	

}
