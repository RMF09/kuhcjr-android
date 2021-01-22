package com.rmf.kuhcjr.Api;

import com.rmf.kuhcjr.Data.SisaCuti;
import com.rmf.kuhcjr.Data.GetAbsensiPegawai;
import com.rmf.kuhcjr.Data.GetCetak;
import com.rmf.kuhcjr.Data.GetHasilPenilaian;
import com.rmf.kuhcjr.Data.GetKantor;
import com.rmf.kuhcjr.Data.GetKegiatan;
import com.rmf.kuhcjr.Data.GetKontak;
import com.rmf.kuhcjr.Data.GetPengumuman;
import com.rmf.kuhcjr.Data.GetPimpinan;
import com.rmf.kuhcjr.Data.GetPostPutFileKegiatan;
import com.rmf.kuhcjr.Data.GetSKP;
import com.rmf.kuhcjr.Data.GetSKPTahun;
import com.rmf.kuhcjr.Data.GetTugas;
import com.rmf.kuhcjr.Data.GetUserLogin;
import com.rmf.kuhcjr.Data.PostPutCuti;
import com.rmf.kuhcjr.Data.PostPutKegiatan;
import com.rmf.kuhcjr.Data.PostPutKontak;
import com.rmf.kuhcjr.Data.PostPutLembur;
import com.rmf.kuhcjr.Data.PostPutPeminjamanKendaraan;
import com.rmf.kuhcjr.Data.PostPutPerjalananDinas;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiInterface {
//    L E M B U R
    @FormUrlEncoded
    @POST("Lembur")
    Call<PostPutLembur> getLembur(@Field("username") String username,@Field("metode") String metode);

    @FormUrlEncoded
    @POST("Lembur")
    Call<PostPutLembur> getDetailLembur(@Field("id") int id);

    @Multipart
    @POST("Lembur")
    Call<PostPutLembur> postLembur(@Part("file\"; filename=\"myfile.pdf\"")RequestBody file,@Part("tanggal") RequestBody tanggal,@Part("date_added") RequestBody date_added,
                                   @Part("mulai") RequestBody mulai,@Part("selesai") RequestBody selesai,
                                   @Part("uraian") RequestBody uraian,@Part("metode") RequestBody metode,
                                   @Part("username") RequestBody username);

    @Multipart
    @POST("Lembur")
    Call<PostPutLembur> putLembur(@Part("file\"; filename=\"myfile.pdf\"")RequestBody file,@Part("id") RequestBody id,@Part("tanggal") RequestBody tanggal,@Part("date_updated") RequestBody date_updated,
                                   @Part("mulai") RequestBody mulai,@Part("selesai") RequestBody selesai,
                                  @Part("uraian") RequestBody uraian,@Part("metode") RequestBody metode,
                                  @Part("username") RequestBody username,@Part("file_pengajuan") RequestBody file_pengajuan_sebelumnya);
    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "Lembur", hasBody = true)
    Call<PostPutLembur> deleteLembur(@Field("id") int id, @Field("file_pengajuan") String file_pengajuan);


    //    C U T I
    @FormUrlEncoded
    @POST("Cuti")
    Call<SisaCuti> getSisaCuti(@Field("username") String username, @Field("metode") String metode);

    @FormUrlEncoded
    @POST("Cuti")
    Call<PostPutCuti> getCuti(@Field("username") String username,@Field("metode") String metode);

    @FormUrlEncoded
    @POST("Cuti")
    Call<PostPutCuti> getDetailCuti(@Field("id") int id);


    @Multipart
    @POST("Cuti")
    Call<PostPutCuti> postCuti(@Part("file\"; filename=\"myfile.pdf\"")RequestBody file,@Part("tanggal") RequestBody tanggal,@Part("date_added") RequestBody date_added,
                               @Part("mulai") RequestBody mulai,@Part("selesai") RequestBody selesai,
                               @Part("uraian") RequestBody uraian,@Part("metode") RequestBody metode,
                               @Part("username") RequestBody username);

    @Multipart
    @POST("Cuti")
    Call<PostPutCuti> putCuti(@Part("file\"; filename=\"myfile.pdf\"")RequestBody file,  @Part("id") RequestBody id, @Part("date_updated") RequestBody date_updated,
                               @Part("mulai") RequestBody mulai,@Part("selesai") RequestBody selesai,
                              @Part("uraian") RequestBody uraian,@Part("metode") RequestBody metode,
                             @Part("username") RequestBody username, @Part("file_pengajuan") RequestBody file_pengajuan_sebelumnya);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "Cuti", hasBody = true)
    Call<PostPutCuti> deleteCuti(@Field("id") int id,@Field("file_pengajuan") String file_pengajuan_sebelumnya);

    //    P E R J A L A N A N      D I N A S
    @FormUrlEncoded
    @POST("Dinas")
    Call<PostPutPerjalananDinas> getDinas(@Field("username") String username,@Field("metode") String metode);

    @FormUrlEncoded
    @POST("Dinas")
    Call<PostPutPerjalananDinas> getDetailDinas(@Field("id") int id);

    @Multipart
    @POST("Dinas")
    Call<PostPutPerjalananDinas> postPerjalananDinas(@Part("file\"; filename=\"myfile.pdf\"")RequestBody file,@Part("tanggal") RequestBody tanggal,@Part("date_added") RequestBody date_added,
                                   @Part("mulai") RequestBody mulai,@Part("selesai") RequestBody selesai,@Part("tujuan") RequestBody tujuan,
                                                     @Part("uraian") RequestBody uraian,@Part("metode") RequestBody metode,
                                                     @Part("username") RequestBody username);

    @Multipart
    @POST("Dinas")
    Call<PostPutPerjalananDinas> putPerjalananDinas(@Part("file\"; filename=\"myfile.pdf\"")RequestBody file,@Part("id") RequestBody id,@Part("tanggal") RequestBody tanggal,@Part("date_updated") RequestBody date_updated,
                                                     @Part("mulai") RequestBody mulai,@Part("selesai") RequestBody selesai,
                                                    @Part("tujuan") RequestBody tujuan,@Part("uraian") RequestBody uraian,
                                                    @Part("metode") RequestBody metode, @Part("username") RequestBody username, @Part("file_pengajuan") RequestBody file_pengajuan_sebelumnya);


    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "Dinas", hasBody = true)
    Call<PostPutPerjalananDinas> deletePerjalananDinas(@Field("id") int id,@Field("file_pengajuan") String file_pengajuan_sebelumnya);


//     P E M I N J A M A N                      K E N D A R A A N
    @FormUrlEncoded
    @POST("PeminjamanKendaraan")
    Call<PostPutPeminjamanKendaraan> getPeminjamanKendaraan(@Field("scan") String scan, @Field("username") String username);

    @FormUrlEncoded
    @POST("PeminjamanKendaraan")
    Call<PostPutPeminjamanKendaraan> postPeminjamanKendaraan(@Field("scan") String scan,
                                                             @Field("username") String username,
                                                             @Field("id_kendaraan") String idKendaraan,
                                                             @Field("tujuan") String tujuan,
                                                             @Field("tanggal_waktu") String tanggal_waktu,
                                                             @Field("tanggal") String tanggal,
                                                             @Field("waktu") String waktu);

    //    K A N T O R
    @GET("Kantor")
    Call<GetKantor> getKantor();

    //    K E G I A T A N
    @FormUrlEncoded
    @POST("Kegiatan")
    Call<GetKegiatan> getKegiatan(@Field("daftar") String daftarkegiatan,
                                  @Field("username") String username);
    @FormUrlEncoded
    @POST("Kegiatan")
    Call<GetKegiatan> getDetailKegiatan(@Field("daftar") String daftarkegiatan,
                                        @Field("username") String username,
                                        @Field("id") int id);

    @FormUrlEncoded
    @POST("Kegiatan")
    Call<PostPutKegiatan> postKegiatan(@Field("tanggal") String tanggal,
                                       @Field("username") String username,
                                       @Field("kegiatan") String kegiatan,
                                       @Field("hasil") String hasil,
                                       @Field("jumlah") int jumlah,
                                       @Field("satuan") String satuan,
                                       @Field("keterangan") String keterangan,
                                       @Field("date_added") String date_added);

    @FormUrlEncoded
    @PUT("Kegiatan")
    Call<PostPutKegiatan> putKegiatan(@Field("tanggal") String tanggal,
                                      @Field("username") String username,
                                      @Field("kegiatan") String kegiatan,
                                      @Field("hasil") String hasil,
                                      @Field("jumlah") int jumlah,
                                      @Field("satuan") String satuan,
                                      @Field("keterangan") String keterangan,
                                      @Field("id") int id,
                                      @Field("date_updated") String date_updated);
    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "Kegiatan", hasBody = true)
    Call<PostPutKegiatan> deleteKegiatan(@Field("id") int id);


    //  F I L E    K E G I A T A N
    @FormUrlEncoded
    @POST("Kegiatan")
    Call<GetPostPutFileKegiatan> getFileKegiatan(@Field("daftar") String daftarFileKegiatan,
                                                 @Field("id") int id);

    @Streaming
    @GET
    Call<ResponseBody> downloadFile(@Url String fileURL);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "Filekegiatan", hasBody = true)
    Call<GetPostPutFileKegiatan> deleteFileKegiatan(@Field("id") int id,@Field("file") String namafile);

    @Multipart
    @POST("Filekegiatan")
    Call<GetPostPutFileKegiatan> postFileKegiatan(@Part MultipartBody.Part file,
                                                  @Part("id_kegiatan") RequestBody idKegiatan,
                                                  @Part("date_added") RequestBody dateAdded);



    //CetakLaporan
    @FormUrlEncoded
    @POST("LaporanKinerja")
    Call<GetCetak> cetakLaporan(@Field("username") String username,
                                @Field("dari_tanggal") String dari_tanggal,
                                @Field("sampai_tanggal") String sampai_tanggal);

    @FormUrlEncoded
    @POST("LoginMobile")
    Call<GetUserLogin> auth(@Field("username") String username,
                            @Field("password") String password);

    @FormUrlEncoded
    @POST("LoginMobile")
    Call<GetUserLogin> authenticating(@Field("username") String username,
                                      @Field("metode") String metode);

    @FormUrlEncoded
    @POST("LoginMobile")
    Call<GetUserLogin> getDataProfile(@Field("username") String username,@Field("metode") String metode);

    @FormUrlEncoded
    @POST("LoginMobile")
    Call<GetPimpinan> getDataPimpinan( @Field("metode") String metode);

    @FormUrlEncoded
    @POST("AbsensiPegawai")
    Call<GetAbsensiPegawai> getCheckWaktu(@Field("check") String check);
    @FormUrlEncoded
    @POST("AbsensiPegawai")
    Call<GetAbsensiPegawai> getCheckAbsen(@Field("username") String username,
                                          @Field("table") String table,
                                          @Field("tanggal") String tanggal);

    @FormUrlEncoded
    @POST("AbsensiPegawai")
    Call<GetAbsensiPegawai> CheckIn(@Field("check") String check,
                                    @Field("username") String username,
                                    @Field("lat_ci") String lat_ci,
                                    @Field("lng_ci") String lng_ci,
                                    @Field("lokasi_ci") String lokasi_ci,
                                    @Field("waktu") String waktu,
                                    @Field("tanggal") String tanggal,
                                    @Field("date_added") String date_added);

    @FormUrlEncoded
    @POST("AbsensiPegawai")
    Call<GetAbsensiPegawai> CheckOut(@Field("check") String check,
                                     @Field("id") int id,
                                     @Field("username") String username,
                                     @Field("lat_co") String lat_co,
                                     @Field("lng_co") String lng_co,
                                     @Field("lokasi_co") String lokasi_co,
                                     @Field("waktu") String waktu,
                                     @Field("tanggal") String tanggal,
                                     @Field("date_updated") String date_updated);
    @FormUrlEncoded
    @POST("AbsensiPegawai")
    Call<PostPutLembur> CheckJadwaLembur(@Field("check") String check,@Field("username") String username);

    @FormUrlEncoded
    @POST("AbsensiPegawai")
    Call<GetAbsensiPegawai> CheckAbsensiLembur(@Field("check") String check,
                                               @Field("checklembur") String checklembur,
                                               @Field("username") String username,
                                               @Field("tanggal") String tanggal);

    @Multipart
    @POST("LoginMobile")
    Call<GetUserLogin> editProfil(@Part("metode") RequestBody metode,
                                  @Part("username") RequestBody username,
                                  @Part MultipartBody.Part file,
                                  @Part("instansi") RequestBody instansi,
                                  @Part("satuan_kerja") RequestBody satuan_kerja,
                                  @Part("unit_kerja") RequestBody unit_kerja,
                                  @Part("sub_unit_kerja") RequestBody sub_unit_kerja,
                                  @Part("alamat") RequestBody alamat,
                                  @Part("nama") RequestBody nama,
                                  @Part("nip") RequestBody nip,
                                  @Part("jabatan") RequestBody jabatan,
                                  @Part("nomor_identitas") RequestBody nomor_identitas,
                                  @Part("no_tlp") RequestBody no_tlp,
                                  @Part("pendidikan_terakhir") RequestBody pendidikan_terakhir,
                                  @Part("no_passport") RequestBody no_passport,
                                  @Part("nama_atasan") RequestBody nama_atasan,
                                  @Part("nip_atasan") RequestBody nip_atasan,
                                  @Part("jabatan_atasan") RequestBody jabatan_atasan,
                                  @Part("file_before") RequestBody file_before);

    @FormUrlEncoded
    @POST("SKPMobile")
    Call<GetSKP> getSKP(@Field("username") String username,@Field("metode") String metode);

    //CetakLaporan
    @FormUrlEncoded
    @POST("SKPMobile")
    Call<GetHasilPenilaian> getHasilPenilaian(@Field("username") String username,
                                               @Field("tahun_id") int tahun_id,
                                               @Field("metode") String metode);
    //    T U G A S
    @FormUrlEncoded
    @POST("SKPMobile")
    Call<GetTugas> getTugas(@Field("metode") String metode,
                            @Field("username") String username,
                            @Field("tahun_id") int tahun_id);
    @FormUrlEncoded
    @POST("SKPMobile")
    Call<GetTugas> tambahTugas(@Field("metode") String metode,
                               @Field("username") String username,
                               @Field("tahun_id") int tahun_id,
                               @Field("kegiatan") String kegiatan,
                               @Field("target_kuant") int kuant,
                               @Field("target_output") String output,
                               @Field("target_kual") int kual,
                               @Field("target_waktu") int waktu,
                               @Field("target_satuan_waktu") String satuan_waktu,
                               @Field("target_biaya") int biaya);
    @FormUrlEncoded
    @POST("SKPMobile")
    Call<GetTugas> editTugas(@Field("metode") String metode,
                               @Field("id") int id,
                               @Field("username") String username,
                               @Field("tahun_id") int tahun_id,
                               @Field("kegiatan") String kegiatan,
                               @Field("target_kuant") int kuant,
                               @Field("target_output") String output,
                               @Field("target_kual") int kual,
                               @Field("target_waktu") int waktu,
                               @Field("target_satuan_waktu") String satuan_waktu,
                               @Field("target_biaya") int biaya);

    @FormUrlEncoded
    @POST("SKPMobile")
    Call<GetTugas> hapusTugas(@Field("metode") String metode,@Field("id") int id_tugas);

    @FormUrlEncoded
    @POST("SKPMobile")
    Call<GetTugas> getDetailTugas(@Field("metode") String metode,@Field("id") int id_tugas);
    @FormUrlEncoded
    @POST("SKPMobile")
    Call<GetSKP> getDetailSKP(@Field("metode") String metode,@Field("id") int id_tugas);
    @FormUrlEncoded
    @POST("SKPMobile")
    Call<GetSKP> prosesPengukuran(@Field("metode") String metode,
                                  @Field("username") String username,
                                  @Field("tahun_id") int tahun_id);
    @FormUrlEncoded
    @POST("SKPMobile")
    Call<GetSKPTahun> tambahTahun(@Field("metode") String metode,
                                  @Field("username") String username,
                                  @Field("tahun_id") int tahun_id);
    @FormUrlEncoded
    @POST("SKPMobile")
    Call<GetSKPTahun> getTahun(@Field("metode") String metode);

    @FormUrlEncoded
    @POST("LoginMobile")
    Call<GetPengumuman> getPengumuman(@Field("metode") String metode);
}
