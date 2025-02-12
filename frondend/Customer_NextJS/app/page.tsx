'use client';
import LandingPage from "@/components/LandingPage/page";

export default function Home() {
  return (
    <main className="flex min-h-screen flex-col items-center">
      <LandingPage 
        bannerData={[]} 
        categoriesData={[]} 
        featuredProducts={[]} 
      />
    </main>
  );
}
