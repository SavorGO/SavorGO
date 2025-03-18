'use client';
import LandingPage from "@/components/LandingPage/page";
import Register from "@/components/login/Register/page";
import { BannerDataType, CategoriesBannerDataType, productType } from "@/lib/interface";

export default function Home() {
  const bannerData: BannerDataType[] = [
    { title: "Ưu đãi đặc biệt", image: "/banner1.jpg" },
  ];

  const categoriesData: CategoriesBannerDataType[] = [
    { id: "1", title: "Món chính", imgSrc: "/category1.jpg", url: "/mon-chinh" },
  ];

  const featuredProducts: productType[] = [
    { 
      id: "1", 
      src: "/bunbo.jpg", 
      title: "Bún bò", 
      price: "50000", 
      quantity: 1, 
      categories: ["Món chính"] 
    },
  ];

  return (
    <main className="flex min-h-screen flex-col items-center">
      <LandingPage 
        bannerData={bannerData} 
        categoriesData={categoriesData} 
        featuredProducts={featuredProducts} 
      />
    </main>
  );
}
